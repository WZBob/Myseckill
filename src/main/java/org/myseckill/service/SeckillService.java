package org.myseckill.service;

import org.myseckill.dto.Exposer;
import org.myseckill.dto.SeckillExcution;
import org.myseckill.entity.Seckill;
import org.myseckill.exception.RepeatKillException;
import org.myseckill.exception.SeckillClosedException;
import org.myseckill.exception.SeckillException;

import java.util.List;

/**
 * 从用户角度设计接口：方法粒度、输入参数（越简单越好）、返回值
 * Created by Zhongbo on 2017/2/19.
 */
public interface SeckillService {

    /**
     * 获取产品列表
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据ID获取产品
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始时暴露描秒杀接口
     * 否则返回系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * Spring声明式事务 执行秒杀
     * @param seckillId
     * @param userPhone
     * @return
     */
    SeckillExcution executeSeckill(long seckillId,long userPhone, String md5) throws SeckillException,RepeatKillException,SeckillClosedException;

    /**
     * 通过数据库存储过程执行秒杀
     * 不需要异常，上边是用异常通知SPRING声明式事务回滚
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillClosedException
     */
    SeckillExcution executeSeckillProcedure(long seckillId,long userPhone, String md5);
}
