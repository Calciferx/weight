package com.calcifer.weight.controller;

import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("device")
@Tag(name = "设备控制", description = "设备控制")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @Operation(summary = "道闸开关", description = "道闸开关")
    @RequestMapping("barrier")
    public RespWrapper<?> barrier(@Parameter(description = "指定设备和状态", required = true) ModBusDeviceEnum device) throws InterruptedException {
        deviceService.controlModBusDevice(device, true);
        Thread.sleep(200);
        deviceService.controlModBusDevice(device, false);
        return new RespWrapper<>(device.getMsg());
    }

    @Operation(summary = "红绿灯", description = "红绿灯")
    @RequestMapping("light")
    public RespWrapper<?> light(@Parameter(description = "指定设备和状态", required = true) ModBusDeviceEnum device, boolean status) {
        deviceService.controlModBusDevice(device, status);
        return new RespWrapper<>(device.getMsg() + ":" + (status ? "红" : "绿"));
    }

    @Operation(summary = "自动称重开关", description = "自动称重开关")
    @RequestMapping("autoweight")
    public RespWrapper<?> autoWeight(@Parameter(description = "是否开启自动称重", required = true) @RequestParam boolean isAuto) {
        Message<WeighEventEnum> message = isAuto ?
                MessageBuilder.withPayload(WeighEventEnum.START_WAIT).build()
                :
                MessageBuilder.withPayload(WeighEventEnum.STOP_WAIT).build();
        weighStateMachine.sendEvent(message);
        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }
}
