-- 数据库初始化脚本，用于数据库创建等工作，方便后续数据库修改的记录

-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
USE seckill;
-- 创建秒杀表；主键id自增，初始值为1000
CREATE TABLE seckill(
seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
name varchar(120) NOT NULL COMMENT '商品名称',
number int NOT NULL COMMENT '库存数量',
start_time timestamp NOT NULL COMMENT '秒杀开始时间',
end_time timestamp NOT NULL COMMENT '秒杀结束时间',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
INSERT INTO seckill(name,number,start_time,end_time)
VALUES
    ('1000元秒杀iphone6','100','2017-03-01 00:00:00','2017-03-02 00:00:00'),
    ('500元秒杀ipad2','200','2017-03-01 00:00:00','2017-03-02 00:00:00'),
    ('300元秒杀小米4','300','2017-03-01 00:00:00','2017-03-02 00:00:00'),
    ('200元秒杀红米note','400','2017-03-01 00:00:00','2017-03-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关的信息
CREATE TABLE success_killed(
seckill_id bigint NOT NULL COMMENT '秒杀商品id',
user_phone bigint NOT NULL COMMENT '用户手机号',
state tinyint NOT NULL DEFAULT -1 COMMENT '状态标示：-1:无效 0：成功 1：已付款 2：已发货',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone),
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 更新秒杀库存表
UPDATE seckill
SET start_time='2017-03-01 00:00:00',
    end_time='2017-03-01 00:00:00';