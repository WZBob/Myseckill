package org.myseckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.myseckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by Zhongbo on 2017/2/18.
 */

// 配置Spring和JUnit的整合，使JUnit启动时加载SpringIOC容器
@RunWith(SpringJUnit4ClassRunner.class)
//指定Spring的配置
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        Date killTime=new Date();
        long id=1000L;
        int count=seckillDao.reduceNumber(id,killTime);
        System.out.println(count);
    }

    @Test
    public void queryById() throws Exception {
        long id=1000L;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }
    //org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
    //在接口声明List<Seckill> queryAll(int offset,int limit);中，java没有记录形参，---->queryAll(arg0,arg1);
    //导致mapper文件中sql语句找不到对应的参数
    //解决办法：接口中，要采用mybatis的Param参数进行标示
    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(0,2);
        for(Seckill seckill: seckills){
            System.out.println(seckill);
        }
    }

}