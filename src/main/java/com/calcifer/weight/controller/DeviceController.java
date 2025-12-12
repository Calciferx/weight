package com.calcifer.weight.controller;

import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @RequestMapping("barrier")
    public String barrier(ModBusDeviceEnum device) throws InterruptedException {
        deviceService.controlModBusDevice(device, true);
        Thread.sleep(200);
        deviceService.controlModBusDevice(device, false);
        return device.getMsg();
    }

    @RequestMapping("light")
    public String light(ModBusDeviceEnum device, boolean status) {
        deviceService.controlModBusDevice(device, status);
        return device.getMsg() + ":" + (status ? "红" : "绿");
    }

    @RequestMapping("autoweight")
    public String autoWeight(@RequestParam boolean isAuto) {
        Message<WeighEventEnum> message = isAuto ?
                MessageBuilder.withPayload(WeighEventEnum.START_WAIT).build()
                :
                MessageBuilder.withPayload(WeighEventEnum.STOP_WAIT).build();
        weighStateMachine.sendEvent(message);
        return "OK";
    }
}
