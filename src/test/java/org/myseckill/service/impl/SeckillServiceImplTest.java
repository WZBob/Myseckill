package org.myseckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.myseckill.dto.Exposer;
import org.myseckill.dto.SeckillExcution;
import org.myseckill.entity.Seckill;
import org.myseckill.exception.RepeatKillException;
import org.myseckill.exception.SeckillClosedException;
import org.myseckill.exception.SeckillException;
import org.myseckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Zhongbo on 2017/2/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list= seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        long id=1000;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id=1001;
        long user=22345123453L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
        if(exposer.isExposed()){
            try {
                SeckillExcution seckillExcution = seckillService.executeSeckill(id,user,exposer.getMd5());
                logger.info("result={}",seckillExcution);
            } catch (SeckillClosedException e){//不讲逻辑异常抛给JUnit
                logger.error(e.getMessage());
            } catch (RepeatKillException e){
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }
        }
        //exposer=Exposer{exposed=true, md5='c6c7728ba79f463bf58f9866f86d9d65', seckillId=1000, nowTime=0, startTime=0, endTime=0}
    }

    /**
     * 为了集成自动化测试，将暴露url与秒杀结合
     * @throws Exception
     */
    @Test
    public void executeSeckill() throws Exception {
        //org.myseckill.exception.RepeatKillException: 重复秒杀异常
        long id=1000;
        long user=12345123453L;
        String md5="c6c7728ba79f463bf58f9866f86d9d65";
        try {
            SeckillExcution seckillExcution = seckillService.executeSeckill(id,user,md5);
            logger.info("result={}",seckillExcution);
        } catch (SeckillClosedException e){//不讲逻辑异常抛给JUnit
            logger.error(e.getMessage());
        } catch (RepeatKillException e){
            logger.error(e.getMessage());
        } catch (SeckillException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void executeSeckillProcedureTest(){
        long id = 1000;
        long userPhone = 13178949903L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExcution seckillExcution = seckillService.executeSeckillProcedure(id,userPhone,md5);
            System.out.println(seckillExcution.getStateInfo());
        }
    }

}