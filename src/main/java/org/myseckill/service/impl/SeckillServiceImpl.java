package org.myseckill.service.impl;

import enums.SeckillStateEnum;
import org.apache.commons.collections.MapUtils;
import org.myseckill.dao.SeckillDao;
import org.myseckill.dao.SuccessKilledDao;
import org.myseckill.dao.cache.RedisDao;
import org.myseckill.dto.Exposer;
import org.myseckill.dto.SeckillExcution;
import org.myseckill.entity.Seckill;
import org.myseckill.entity.SuccessKilled;
import org.myseckill.exception.RepeatKillException;
import org.myseckill.exception.SeckillClosedException;
import org.myseckill.exception.SeckillException;
import org.myseckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhongbo on 2017/2/19.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private final String slat="wewqreijweifj*(&^&2324";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化：缓存，降低数据库访问
        /**
         * get data from cache
         * if data = null
         *      get from db
         *      put cache
         *  logic
         */
        Seckill seckill = redisDao.getSeckill(seckillId);//查询缓存
        if (seckill == null){
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                //查询失败
                return new Exposer(false, seckillId);
            }else {
                //放入缓存
                redisDao.putSeckill(seckill);
            }
        }
        Date now = new Date();
        Date start = seckill.getStartTime();
        Date end = seckill.getEndTime();
        //秒杀未开启
        if (now.getTime() < start.getTime()
                || now.getTime() > end.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), start.getTime(), end.getTime());
        }
        //秒杀开启，暴露接口
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 执行秒杀：减库存  +   添记录
     * 采用事务保证一致性
     * @param seckillId
     * @param userPhone
     * @return
     */
    @Transactional
    public SeckillExcution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillClosedException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("秒杀数据被篡改");
        }
        //减库存
        Date now = new Date();
        try {
            //添加购买记录
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatKillException("重复秒杀异常");
            } else {
                //不是重复秒杀，执行减库存
                int reduceCount = seckillDao.reduceNumber(seckillId, now);
                if (reduceCount <= 0) {
                    throw new SeckillClosedException("秒杀已结束异常");//可能产品已售完
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillClosedException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (SeckillException e3) {
            throw e3;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SeckillException("内部异常");
        }
    }

    /**
     * 存储过程执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    public SeckillExcution executeSeckillProcedure(long seckillId, long userPhone, String md5){
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExcution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String ,Object>();
        map.put("seckillId",seckillId);
        map.put("userPhone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);//作为返回值
        //执行存储过程，为什么需要map，因为需要将result传入存储过程
        try{
            seckillDao.killByProcedure(map);
            //该方式比较成熟
            int result = MapUtils.getIntValue(map,"result",-2);
            if(result == 1){
                //秒杀成功
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExcution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExcution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            return new SeckillExcution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }
}
