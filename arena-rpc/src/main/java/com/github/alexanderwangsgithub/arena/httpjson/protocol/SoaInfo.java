package com.github.alexanderwangsgithub.arena.httpjson.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanggang on 8/22/16.
 */
@Data
public class SoaInfo {
    private String ver;
    private final static String REQ_KEY = "req";
    private final static String RPC_KEY = "rpc";

    private Map<String,String> soaInfo;

    @JsonIgnore
    public String getReqId() {
        return get(REQ_KEY);
    }

    @JsonIgnore
    public void setReqId(String reqId) {
        set(REQ_KEY, reqId);
    }

    @JsonIgnore
    public String getRpcId() {
        return get(RPC_KEY);
    }

    @JsonIgnore
    public void setRpcId(String rpcId) {
        set(RPC_KEY, rpcId);
    }

    @JsonIgnore
    public String get(String key) {
        return soaInfo == null ? null : soaInfo.get(key);
    }

    @JsonIgnore
    public void set(String key, String value) {
        if (soaInfo == null) {
            soaInfo = new HashMap<>();
        }
        soaInfo.put(key, value);
    }

}
