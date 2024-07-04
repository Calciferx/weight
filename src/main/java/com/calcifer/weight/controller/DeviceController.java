package com.calcifer.weight.controller;

import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

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
}
