package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.dto.WeightInfo;
import com.calcifer.weight.entity.po.TruckInfo;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.CardMapper;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.utils.SerialPortUtil;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutoScanJob {
    private static final Double MINWEIGHT = 6000D;
    private static final Double MAXWEIGHT = 100000D;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private VoiceService voiceService;
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    private LinkedBlockingQueue<WeightInfo> queue = new LinkedBlockingQueue<>();

    @Autowired
    private CardMapper cardMapper;
    @Value("${calcifer.weight.enable-auto-scan:true}")
    private boolean enableAutoScan;

    @PostConstruct
    public void init() {
        weighStateMachine.start();
    }

    @Scheduled(fixedDelay = 500)
    public void autoScan() throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
        if (!enableAutoScan) return;
        DeviceService.ModBusDeviceStatus modBusDeviceStatus = deviceService.readModBusDeviceStatus();
        if (modBusDeviceStatus.isInfrared1()) {
            Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.TRUCK_FOUND).setHeader("reverse", false).build();
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

    @Bean
    private SerialPortUtil.DataAvailableListener cardListener() {
        return serialPortEvent -> {
            byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
            String dataHex = new BigInteger(1, data).toString(16);
            if (dataHex.length() == 36 && ("1100EE00").equals(dataHex.substring(0, 8))) {
                String cardNum = dataHex.substring(8, 32);
                log.info("read cardNum: {}", cardNum);
                TruckInfo truckInfo = cardMapper.getTruckInfo(cardNum);
                if (truckInfo != null && ("启用").equals(truckInfo.getBackup14())) {
                    log.info("read truckInfo success");
                    Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.READ_CARD).setHeader("truckInfo", truckInfo).build();
                    weighStateMachine.sendEvent(message);
                } else {
                    voiceService.voice("未读到卡，或此卡没注册，请联系管理员");
                }
            }
        };
    }

    @Bean
    private SerialPortUtil.DataAvailableListener scaleListener() {
        return serialPortEvent -> {
            byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
            String dataHex = new BigInteger(1, data).toString(16);
            String validDataHex = dataHex.substring(dataHex.indexOf("02"), dataHex.indexOf("0D") + 2);
            if (validDataHex.length() > 32) {
                String status = dataHex.substring(4, 6);

                String weightHex = validDataHex.substring(8, 20);
                StringBuilder weightSB = new StringBuilder();
                boolean bFlag = true;
                for (int i = 0; i < weightHex.length() / 2; i++) {
                    char childChar = hexToChar(weightHex, i * 2, (i + 1) * 2);
                    if (childChar >= 30 && childChar <= 0x39) {
                        weightSB.append(childChar);
                    } else {
                        bFlag = false;
                        break;
                    }
                }

                if (bFlag) {
                    Map<String, Object> map = new HashMap<>();
                    String weight = weightSB.toString().trim();
                    WeightInfo weightInfo = new WeightInfo(status, dataHex);
                    map.put("dataHex", weight);
                    map.put("type", "7");
                    map.put("map", weightInfo);
                    if (queue.size() > 30) {
                        queue.poll();
                        try {
                            queue.put(weightInfo);
                        } catch (InterruptedException e) {
                            log.info("queue put exception", e);
                        }
                    } else {
                        try {
                            queue.put(weightInfo);
                        } catch (InterruptedException e) {
                            log.info("queue put exception", e);
                        }
                    }
                    webSocketHandler.sendJsonToAllUser(map);
                    List<Integer> dataList = queue.stream().map(o -> Integer.valueOf(o.getDataHex())).collect(Collectors.toList());
                    if (dataList.stream().max(Comparator.comparingInt(o -> o)).get() - dataList.stream().min(Comparator.comparingInt(o -> o)).get() < 10) {
                        Double average = dataList.stream().collect(Collectors.averagingInt(o -> o));
                        if (average > MINWEIGHT && average < MAXWEIGHT) {
                            Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.WEIGHED).setHeader("weight", average).build();
                            weighStateMachine.sendEvent(message);
                        }
                    }
                }
            }
        };
    }

    private char hexToChar(String str, int x, int y) {
        if (x > str.length())
            return ' ';
        int num;
        if (y > str.length()) {
            num = Integer.parseInt(str.substring(x), 16);
        } else {
            num = Integer.parseInt(str.substring(x, y), 16);
        }
        return (char) num;
    }
}
