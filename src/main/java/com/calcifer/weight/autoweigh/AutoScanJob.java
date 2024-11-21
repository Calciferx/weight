package com.calcifer.weight.autoweigh;

import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.service.ModbusDeviceService;
import com.calcifer.weight.service.SerialDeviceService;
import com.calcifer.weight.service.VoiceService;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public class AutoScanJob {


    @Autowired
    private ModbusDeviceService modbusDeviceService;
    @Autowired
    private SerialDeviceService serialDeviceService;
    @Autowired
    private VoiceService voiceService;
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @Value("${calcifer.weight.enable-auto-scan:true}")
    private boolean enableAutoScan;

    @Value("${calcifer.weight.wait-time:60000}")
    private int waitTime;

    @PostConstruct
    public void init() {
        weighStateMachine.start();
    }

    @Scheduled(fixedDelay = 500)
    public void autoScan() throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
        synchronized (AutoScanJob.class) {
            if (!enableAutoScan) return;
            if (!modbusDeviceService.isInit()) {
                modbusDeviceService.init();
                return;
            }
            if (!serialDeviceService.isInit()) {
                serialDeviceService.init();
                return;
            }
            ModbusDeviceService.ModBusDeviceStatus modBusDeviceStatus = modbusDeviceService.readModBusDeviceStatus();
            // 任意一个红外被遮挡时重置计时变量
            long currentTimeMillis = System.currentTimeMillis();
            if (modBusDeviceStatus.isInfrared1() || modBusDeviceStatus.isInfrared2() || modBusDeviceStatus.isInfrared3() || modBusDeviceStatus.isInfrared4()) {
                WeightContext.lastStatusChange = currentTimeMillis;
            } else if (currentTimeMillis - WeightContext.lastStatusChange > waitTime && weighStateMachine.getState().getId() != WeighStatusEnum.WAIT) {
                // 超过waitTime没有状态变化自动重置系统
                voiceService.voice("长时间未检测到车辆，系统复位");
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.RESET).build();
                weighStateMachine.sendEvent(message);
            }
            if (modBusDeviceStatus.isInfrared1()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.TRUCK_FOUND).setHeader("reverse", false).build();
                weighStateMachine.sendEvent(message);
            }
            if (!modBusDeviceStatus.isInfrared1()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.CANCEL_ENTER).build();
                weighStateMachine.sendEvent(message);
            }
            if (modBusDeviceStatus.isInfrared4()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.TRUCK_FOUND).setHeader("reverse", true).build();
                weighStateMachine.sendEvent(message);
            }
            if (modBusDeviceStatus.isInfrared2()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.ENTER).build();
                weighStateMachine.sendEvent(message);
            }
            if (!modBusDeviceStatus.isInfrared2()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.ENTERED).build();
                weighStateMachine.sendEvent(message);
            }
            if (modBusDeviceStatus.isInfrared3()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.LEAVING_WEIGH).build();
                weighStateMachine.sendEvent(message);
            }
            if (!modBusDeviceStatus.isInfrared3()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.LEFT_WEIGH).build();
                weighStateMachine.sendEvent(message);
            }
            if (modBusDeviceStatus.isInfrared4()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.LEAVING).build();
                weighStateMachine.sendEvent(message);
            }
            if (!modBusDeviceStatus.isInfrared4()) {
                Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.LEFT).build();
                weighStateMachine.sendEvent(message);
            }
        }
    }
}
