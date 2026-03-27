package com.xiaocheng.netdisk.common.exception;

import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.utils.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        return ApiResponse.error(ResponseCode.FAIL.getCode(), "系统异常：" + e.getMessage());
    }
}
