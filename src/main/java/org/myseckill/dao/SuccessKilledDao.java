package org.myseckill.dao;

import org.apache.ibatis.annotations.Param;
import org.myseckill.entity.SuccessKilled;

/**
 * Created by Zhongbo on 2017/2/18.
 */
public interface SuccessKilledDao {
    /**
     * 根据产品ID和用户身份插入秒杀记录,可以过滤重复（联合主键）
     * @param seckillId
     * @param userPhone
     * @return 返回插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据产品ID和用户身份查询秒杀记录，并返回产品信息
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
