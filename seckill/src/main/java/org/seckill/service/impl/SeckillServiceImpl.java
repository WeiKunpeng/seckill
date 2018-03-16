package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/*
 * weikunpeng
 * 2018/3/8 19:44
 *
 * @Component 统称一个组建实例
 * @Service
 * @Dao
 * @Conroller
 */

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    //注入service依赖

    @Autowired  //@Resource @Inject
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //用于混淆md5
    private final String slat="jfjooi02isdalla;kd;kp-%^%721hSw";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null)
        {
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime()<startTime.getTime() ||nowTime.getTime()>endTime.getTime())
        {
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转换特定字符串过程  不可逆
        String md5=getMD5(seckillId);//TODO
        return new Exposer(true,md5,seckillId);



    }

    private String getMD5(long seckillId){
        String base =seckillId+"/"+slat;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return  md5;
    }
    @Transactional

    /**
     * 使用注解控制事务方法
     * 优点：
     * 1、开发团队达成一致约定，明确标注事务方法的编程的风格
     * 2、保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，RPC//HTTP请求或者剥离到事务方法外部
     * 3、不是所有的方法都需要事务,如只有一条修改操作，只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        if(md5==null||!md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //秒杀逻辑 减库存+记录购买行为
        Date nowTime=new Date();

        try {
            int updateCount=seckillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0)throw new SeckillCloseException("seckill is closed");
            else{
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount<=0) throw new RepeatKillException("seckill repeated");
                else{
                    SuccessKilled successKilled =successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }

        }
        catch (SeckillCloseException e1){
            throw  e1;

        }
        catch (RepeatKillException e2){
            throw  e2;

        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new SeckillException("seckill inner error:"+e.getMessage());
            //所有编译异常转换成runtime异常
        }



    }
}
