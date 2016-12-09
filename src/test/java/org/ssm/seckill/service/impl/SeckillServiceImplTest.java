package org.ssm.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.ssm.seckill.dto.Exposer;
import org.ssm.seckill.dto.SeckillExecution;
import org.ssm.seckill.entity.Seckill;
import org.ssm.seckill.exception.RepeatKillException;
import org.ssm.seckill.exception.SeckillCloseException;
import org.ssm.seckill.service.SeckillService;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        for (Seckill s : seckillList) {
            System.out.println(s);
        }
    }

    @Test
    public void testGetById() throws Exception {
        Seckill seckill = seckillService.getById(1000);
        System.out.println(seckill);
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);
        } else {
            // 秒杀未开启
            logger.error("exposer={}", exposer);
        }

    }

    @Test
    public void testExecuteSeckill() throws Exception {
        //exposer=Exposer{exposed=true, md5='9024a5568837b282ea5c17f92600c859', seckillId=1001, now=0, start=0, end=0}
        long id = 1001;
        long phone = 13631231234L;
        String md5 = "9024a5568837b282ea5c17f92600c859";

        try {
            SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
            logger.info("execution={}", execution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * 整合 testExportSeckillUrl（暴露秒杀接口） 和 testExecuteSeckill（执行秒杀） 测试方法
     *
     * @throws Exception
     */
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);
            long phone = 13631231234L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}", execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            // 秒杀未开启
            logger.error("exposer={}", exposer);
        }
    }
}