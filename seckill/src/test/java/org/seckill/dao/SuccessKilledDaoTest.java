package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合 junit启动时加载springIOC容器
 * spring-test junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        int i=successKilledDao.insertSuccessKilled(1000L,15927368658L);
        System.out.println(i);


    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(1001L,15927368658L);
        System.out.println(successKilled.toString());
    }
}