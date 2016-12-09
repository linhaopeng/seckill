package org.ssm.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by baojulin on 2016/12/7.
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillCloseException(String message) {
        super(message);
    }
}
