package org.myseckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.dyuproject.protostuff.ProtobufIOUtil;
import org.myseckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Zhongbo on 2017/2/24.
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JedisPool jedisPool;//类似数据库连接池

    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    //根据class文件创建schema，创建模式；通过字节码传递属性
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    /**
     * 缓存获取数据
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId){
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String keys = "seckill:"+seckillId;
                //Redis内部没有实现序列化,二进制
                //get -> bytes[]   反序列获得对象
                //自定义序列化
                //protostuff序列化对象必须是pojo
                byte[] bytes = jedis.get(keys.getBytes());//根据key获取缓存数据
                if(bytes != null){
                    //创建一个空对象，用于获取缓存中的数据
                    Seckill seckill = schema.newMessage();
                    //给空对象赋值
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 数据加入缓存
     * @param seckill
     * @return
     */
    public String putSeckill(Seckill seckill){
        //对象 -》byte[] -》发给redis

        try {
            Jedis jedis = jedisPool.getResource();
            try{
                String keys = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//为大对象添加缓存器
                int timeout = 60*60;//缓存1小时
                String result = jedis.setex(keys.getBytes(),timeout,bytes);//超时缓存
                return result;//正常情况下返回OK
            }finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
