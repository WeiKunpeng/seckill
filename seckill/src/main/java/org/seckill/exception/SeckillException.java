package org.seckill.exception;

/*
 * weikunpeng
 * 2018/3/8 19:40
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
