package com.calcifer.weight.interceptor;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${calcifer.weight.debug:false}")
    private boolean debug;

    @ExceptionHandler
    public RespWrapper<?> handleException(Exception e) {
        if (debug) {
            log.error(e.getMessage(), e);
            return new RespWrapper<>(false, RespCodeEnum.FAILED, e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        log.error(e.getMessage(), e);
        return new RespWrapper<>(false, RespCodeEnum.FAILED, e.getMessage());
    }
}
