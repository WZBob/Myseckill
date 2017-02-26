package org.myseckill.dao;

import org.apache.ibatis.annotations.Param;
import org.myseckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhongbo on 2017/2/18.
 */
public interface SeckillDao {

    /**
     * 根据产品ID和秒杀时间减库存
     * 注意需要采用@Param(" ")对方法的形参进行标识，便于在MyBatis中SQL语句获取数据
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据产品ID查询产品信息
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 根据偏移量和条目数查询多个产品信息
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    void killByProcedure(Map<String,Object> params);
}
