package org.myseckill.dto;

/**
 * 返回结果的封装
 * Created by Zhongbo on 2017/2/20.
 */
public class SeckillResult<T> {
    //是否成功
    private boolean success;

    //具体数据
    private T data;

    //错误信息
    private String error;

    /**
     * 秒杀成功结果
     * @param success
     * @param data
     */
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 秒杀失败结果
     * @param success
     * @param error
     */
    public SeckillResult(boolean success, String error) {

        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "SeckillResult{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
