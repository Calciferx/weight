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

    @Operation(summary = "初始化设备", description = "初始化所有设备状态")
    @RequestMapping("init")
    public RespWrapper<?> init() {
        // 1. 发送 RESET 事件并获取是否被接受
        boolean accepted = weighStateMachine.sendEvent(MessageBuilder.withPayload(WeighEventEnum.RESET).build());

        // 2. 检查事件是否被接受以及执行过程中是否有异常
        if (!accepted) {
            return new RespWrapper<>(false, RespCodeEnum.FAILED, "设备初始化失败：重置指令未被接受");
        }
        if (weighStateMachine.hasStateMachineError()) {
            // 尝试从 ExtendedState 获取具体的错误信息（需在状态机 Action 中通过 context.getExtendedState().getVariables().put("ERROR_MSG", ...) 存入）
            String errorMsg = weighStateMachine.getExtendedState().get("ERROR_MSG", String.class);
            if (errorMsg != null) {
                weighStateMachine.getExtendedState().getVariables().remove("ERROR_MSG");
                return new RespWrapper<>(false, RespCodeEnum.FAILED, errorMsg);
            }
        }

        return new RespWrapper<>(true, RespCodeEnum.SUCCESS);
    }
}
