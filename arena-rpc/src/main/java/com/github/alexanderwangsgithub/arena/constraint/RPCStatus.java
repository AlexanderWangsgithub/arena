package com.github.alexanderwangsgithub.arena.constraint;


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
