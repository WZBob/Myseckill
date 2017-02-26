-- 存储过程执行秒杀
-- 声明结束符 控制台由 ；转为 $$
DELIMITER $$
-- 创建过程
-- in为输入参数，out为输出
-- row_count():返回上一条修改类型SQL（delete update insert）语句影响的行数
-- row_count()：0：未修改数据； <0 : sql错误 ； >0 : 影响的行数
CREATE PROCEDURE seckill.execute_seckill
 (in v_seckill_id bigint, in v_phone bigint,
    in v_kill_time timestamp, out r_result int)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    insert ignore into success_killed
      (seckill_id,user_phone,create_time)
      values(v_seckill_id,v_phone,v_kill_time);
    select row_count() into insert_count;
    IF(insert_count = 0) THEN
      ROLLBACK;
      set r_result = -1;
    ELSEIF(insert_count < 0) THEN
      ROLLBACK ;
      set r_result = -2;
    ELSE
      update seckill
      set number = number-1
      where seckill_id = v_seckill_id
        and end_time > v_kill_time
        and start_time < v_kill_time
        and number > 0;
      select row_count() into insert_count;
      IF (insert_count = 0) THEN
        ROLLBACK ;
        set r_result=0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK ;
        set r_result =-2;
      ELSE
        COMMIT ;
        set r_result = 1;
      END IF;
    END IF;
  END
$$

-- 恢复结束符号
DELIMITER ;

-- 设置参数
set @r_result = -3;
-- 执行存储过程
call seckill.execute_seckill(1003,13165455503,now(),@r_result);
--查看参数值
select @r_result;