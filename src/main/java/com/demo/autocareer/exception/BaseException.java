package com.demo.autocareer.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Data;

@Data
public class BaseException extends RuntimeException {
   private final String code;
    private final String message;
    private final int status;
    private Map<String, String> params;

    public BaseException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
        this.params = new HashMap<>();
    }

    public BaseException(String code, String message, int status, Map<String, String> params) {
        super(message);
        this.code = code;
        this.message = message;
        this.status = status;
        this.params = params != null ? params : new HashMap<>();
    }

    public void addParam(String key, String value) {
        if (Objects.isNull(params)) {
            params = new HashMap<>();
        }
        params.put(key, value);
    } 

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getParams(){
        return  params;
    }

}
