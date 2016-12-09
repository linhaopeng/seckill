package org.ssm.seckill.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.ssm.seckill.dao.SeckillDao;
import org.ssm.seckill.dao.SuccessKilledDao;
import org.ssm.seckill.dto.Exposer;
import org.ssm.seckill.dto.SeckillExecution;
import org.ssm.seckill.entity.Seckill;
import org.ssm.seckill.entity.SuccessKilled;
import org.ssm.seckill.enums.SeckillStateEnum;
import org.ssm.seckill.exception.RepeatKillException;
import org.ssm.seckill.exception.SeckillCloseException;
import org.ssm.seckill.exception.SeckillException;
import org.ssm.seckill.service.SeckillService;

import java.util.Date;
import java.util.List;

/**
 * Created by baojulin on 2016/12/7.
 */
@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 注入Service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    // md5盐值字符串，用于混淆MD5
    private final String slat = "suibianluanandeyidw2341";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 100);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

        if (seckill == null) {
            // 未开启秒杀
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统当前时间
        Date nowTime = new Date();

        // 秒杀未开启，或者秒杀已经结束
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            // md5被篡改
            throw new SeckillException("seckill data rewrite");
        }

        // 执行秒杀逻辑：减库存 + 记录购买行为
        Date now = new Date();
        try {
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, now);
                if (updateCount <= 0) {
                    // 没有更新到记录 rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    // 秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWidthSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }

            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常转换为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
