package com.demo.autocareer.utils;

import java.util.Map;

import com.demo.autocareer.exception.BaseException;
import com.demo.autocareer.exception.ErrorCode;

public class ExceptionUtil {
    public static BaseException fromErrorCode(ErrorCode errorCode){
        return new BaseException(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus()
        );
    }

    public static BaseException fromErrorCode(ErrorCode errorCode, Map<String, String> params) {
        return new BaseException(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getStatus(),
                params
        );
    }
}
