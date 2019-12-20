package com.flying.cattle.me.disruptor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.ExceptionHandler;

public class MyHandlerException implements ExceptionHandler<Object> {

    private Logger logger = LoggerFactory.getLogger(MyHandlerException.class);

    /*
     * (non-Javadoc) 运行过程中发生时的异常
     *
     * @see
     * com.lmax.disruptor.ExceptionHandler#handleEventException(java.lang.Throwable
     * , long, java.lang.Object)
     */
    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        ex.printStackTrace();
        logger.error("process data error sequence ==[{}] event==[{}] ,ex ==[{}]", sequence, event.toString(), ex.getMessage());
    }

    /*
     * (non-Javadoc) 启动时的异常
     *
     * @see
     * com.lmax.disruptor.ExceptionHandler#handleOnStartException(java.lang.
     * Throwable)
     */
    @Override
    public void handleOnStartException(Throwable ex) {
        logger.error("start disruptor error ==[{}]!", ex.getMessage());
    }

    /*
     * (non-Javadoc) 关闭时的异常
     *
     * @see
     * com.lmax.disruptor.ExceptionHandler#handleOnShutdownException(java.lang
     * .Throwable)
     */
    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.error("shutdown disruptor error ==[{}]!", ex.getMessage());
    }

}
