package org.myseckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.myseckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Zhongbo on 2017/2/18.
 */
// 配置Spring和JUnit的整合，使JUnit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//指定Spring的配置
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long id=1000L;
        long user=12345678902L;
        int count = successKilledDao.insertSuccessKilled(id,user);
        System.out.println(count);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id=1000L;
        long user=12345678901L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,user);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());//产品信息
    }

}