package org.ssm.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ssm.seckill.entity.SuccessKilled;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long seckillId = 1000;
        long userPhone = 13800000000L;
        int i = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println("更新记录数：" + i);
    }

    @Test
    public void testQueryByIdWidthSeckill() throws Exception {
        long seckill = 1000;
        long userPhone = 13800000000L;
        SuccessKilled successKilled = successKilledDao.queryByIdWidthSeckill(seckill, userPhone);
        System.out.println(successKilled);
    }
}