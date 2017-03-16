package com.github.alexanderwangsgithub.arena.constraint;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 18/01/2017
 */
public enum RPCStatus {
    /** 业务异常*/
    business_exec,

    /** 非业务异常*/
    non_business_exec,

    /** 请求超时*/
    rpc_timeout,

    /** 熔断*/
    circuit_breaker,

    /** 降级*/
    rpc_downgrade,

    /** 授权异常*/
    authority_exec,

    /** 超出服务负载*/
    no_available_thread;
}
