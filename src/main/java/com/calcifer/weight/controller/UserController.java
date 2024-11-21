package com.calcifer.weight.controller;

import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述: 用户
 */

@Slf4j
@RequestMapping("/sys/user")
@RestController
@Api(basePath = "/sys/user", value = "系统 用户模块", description = "系统", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class UserController {

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
