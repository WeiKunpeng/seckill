package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/*
 * weikunpeng
 * 2018/3/8 21:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                       "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    @Test
    public void getSeckillList() {

        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);

    }


    @Test
    public void getById() {

        Seckill seckill = seckillService.getById(1000L);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() {

        long id = 2;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        /*
        exposed=true, md5='85d2cf1e94fdb65107439a39e182df0b',
        seckillId=1000, now=0, start=0, end=0
         */
    }

    @Test
    public void executeSeckill() {

        long id = 1;
        long phone = 156156153165L;
        String md5 = "85d2cf1e94fdb65107439a39e182df0b";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
        logger.info("result={}", seckillExecution);

        /**
         * result=SeckillExecution{seckillId=1,
         * state=1, stateInfo='秒杀成功',
         * successKilled=SuccessKilled{seckillId=1,
         * userPhone=156156153165, state=-1, createTime=Sat Mar 10 15:16:34 CST 2018}}
         */
    }

    //测试代码完整逻辑 注意重复执行
    @Test
    public void testSeckillLogic() {
        long id = 2;
        Exposer exposer = seckillService.exportSeckillUrl(id);


        if(exposer.isExposed()) {
            logger.info("exposer={}", exposer);
            long phone = 156156153165L;
            String md5 = exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());

            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());

            }
        }
            else{
            logger.warn("exposer={}",exposer);

            }


        }

    }
