package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.annotation.Resource;

import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合 junit启动时加载springIOC容器
 * spring-test junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {


    //注入依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception{

        long id =1000;
        Seckill seckill=seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void reduceNumber() throws Exception{

        Date killTime=new Date();
        int updateCount=seckillDao.reduceNumber(1L,killTime);
        System.out.println(updateCount);
        /**
         * 23:15:18.010 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@533bda92] will not be managed by Spring
         23:15:18.051 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number -1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0;
         23:15:18.307 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2018-03-06 23:15:17.442(Timestamp), 2018-03-06 23:15:17.442(Timestamp)
         23:15:18.365 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 0
         */

    }



    @Test
    public void queryAll() throws Exception{

        List<Seckill> seckills =seckillDao.queryAll(0,100);
        for(Seckill seckill:seckills)
        {
            System.out.println(seckill);
        }
        /**
         * Seckill{seckillId=1000, name='1000元秒杀iphone6', number=100, startTime=Sun Nov 01 00:00:00 CST 2015, endTime=Mon Nov 02 00:00:00 CST 2015, createTime=Mon Mar 05 16:42:34 CST 2018}
         Seckill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Sun Nov 01 00:00:00 CST 2015, endTime=Mon Nov 02 00:00:00 CST 2015, createTime=Mon Mar 05 16:42:34 CST 2018}
         Seckill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Sun Nov 01 00:00:00 CST 2015, endTime=Mon Nov 02 00:00:00 CST 2015, createTime=Mon Mar 05 16:42:34 CST 2018}
         Seckill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Sun Nov 01 00:00:00 CST 2015, endTime=Mon Nov 02 00:00:00 CST 2015, createTime=Mon Mar 05 16:42:34 CST 2018}
         */


    }
}