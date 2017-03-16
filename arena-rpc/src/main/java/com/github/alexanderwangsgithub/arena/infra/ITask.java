package com.github.alexanderwangsgithub.arena.infra;

import com.github.alexanderwangsgithub.arena.constraint.RPCStatus;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 18/01/2017
 */
public interface ITask extends Callable<Object> {
    Method getMethod();

    default long getTimeoutInMillis() {
        return 0;
    }

    RPCStatus getStatus();

    void setStatus(RPCStatus status);

    boolean supportsFallback();

    Object callFallback() throws Throwable;

    void cancel();
}