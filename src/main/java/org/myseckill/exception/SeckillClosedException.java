package org.myseckill.exception;

/**
 * Created by Zhongbo on 2017/2/19.
 */
public class SeckillClosedException extends SeckillException {
    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
