package com.calcifer.weight.controller;

import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("admin")
public class SlaveController {
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @PostMapping(value = "initSystem")
    @ApiOperation(value = "初始化系统", notes = "条件：无", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespWrapper<Boolean> initSystem(
            @ApiParam(required = true, value = "token") @RequestParam(required = true) String token,
            HttpServletRequest request) throws ModbusIOException {
        log.info("init system...send reset event");
        log.info("now state machine status is {}", weighStateMachine.getState());
        weighStateMachine.sendEvent(WeighEventEnum.RESET);
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }

    @ExceptionHandler
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
