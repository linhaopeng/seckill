package org.ssm.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.ssm.seckill.entity.SuccessKilled;

/**
 * Created by baojulin on 2016/12/7.
 */
public interface SuccessKilledDao {
    /**
     * 插入购买明细，可以过滤重复
     *
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据秒杀商品的id查询明细Successkilled对象（该对象携带了Seckill秒杀产品对象）
     *
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWidthSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
