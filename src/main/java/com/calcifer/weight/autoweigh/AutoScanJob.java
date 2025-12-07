package com.calcifer.weight.autoweigh;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.dto.WeightInfo;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.po.TruckInfo;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.CardMapper;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.utils.SerialPortUtil;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.util.HexUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutoScanJob {

    @Value("${calcifer.weight.min-weight:1000}")
    private Double minWeight;
    @Value("${calcifer.weight.max-weight:100000}")
    private Double maxWeight;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private VoiceService voiceService;
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    private final Queue<WeightInfo> queue = new LinkedList<>();

    @Autowired
    private CardMapper cardMapper;
    @Value("${calcifer.weight.enable-auto-scan:true}")
    private boolean enableAutoScan;
    @Value("${calcifer.weight.sampled-time:30}")
    private int sampledTime;
    @Value("${calcifer.weight.wait-time:60000}")
    private int waitTime;
    @Value("${calcifer.weight.ignore-stable-flag:false}")
    private boolean ignoreStableFlag;
    @Value("${calcifer.weight.stable-range:20}")
    private int stableRange;

    @PostConstruct
    public void init() {
        weighStateMachine.start();
    }

    @Scheduled(fixedDelay = 500)
    public void autoScan() throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
        synchronized (AutoScanJob.class) {
            if (!enableAutoScan) return;
            if (!deviceService.isInit()) {
                deviceService.init();
                return;
            }
            DeviceService.ModBusDeviceStatus modBusDeviceStatus = deviceService.readModBusDeviceStatus();
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

    @Bean
    public SerialPortUtil.DataAvailableListener cardListener() {
        return serialPortEvent -> {
            try {
                byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
                String dataHex = Convert.toHex(data).toUpperCase();
                log.info("read card data: {}", dataHex);
                Pattern pattern = Pattern.compile("1100EE00.{28}");
                Matcher matcher = pattern.matcher(dataHex);
                if (matcher.find()) {
                    String cardNum = matcher.group().substring(8, 32);
                    //                String cardNum = "E2000016660E015616306EDE";
                    log.info("read cardNum: {}", cardNum);
                    TruckInfo truckInfo = cardMapper.getTruckInfo(cardNum);
                    if (truckInfo != null && ("启用").equals(truckInfo.getBackup14())) {
                        log.info("read truckInfo success");
                        Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.READ_CARD).setHeader("truckInfo", truckInfo).build();
                        weighStateMachine.sendEvent(message);
                    } else {
                        log.error("card not register!!");
                        voiceService.voice("此卡未注册，请联系管理员");
                    }
                }
            } catch (Exception e) {
                log.error("READ CARD EXCEPTION!", e);
            }
        };
    }

    @Bean
    public SerialPortUtil.DataAvailableListener scaleListener() {
        return serialPortEvent -> {
            try {
                byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
                String dataHex = Convert.toHex(data).toUpperCase();
                log.debug("read scale data: {}", dataHex);
                Pattern pattern = Pattern.compile("02.{30}0D");
                Matcher matcher = pattern.matcher(dataHex);
                while (matcher.find()) {
                    // 截取有效输出
                    String validHex = matcher.group();
                    // 提取重量信息
                    String numHex = validHex.substring(8, 20);
                    String numStr = Convert.hexStrToStr(numHex, StandardCharsets.US_ASCII).trim();
                    String status = dataHex.substring(4, 6);
                    if (numStr.matches("\\d+")) {
                        int num = Integer.parseInt(numStr);
                        // 如果称上有东西则不开始计时，小于100的不算
                        if (num > 100) {
                            WeightContext.lastStatusChange = System.currentTimeMillis();
                        }
                        WeightInfo weightInfo = new WeightInfo(status, num);
                        WSRespWrapper<WeightInfo> rtWeightInfo = new WSRespWrapper<>(weightInfo, WSCodeEnum.RT_WEIGH_NUM);
                        log.debug("weight map: {}", JSON.toJSONString(rtWeightInfo));
                        // 如果称的状态为稳定则开始采样计算重量，否则清空重量数据队列
                        if (ignoreStableFlag) {
                            if (queue.size() < sampledTime) {
                                queue.offer(weightInfo);
                            } else {
                                List<Integer> dataList = queue.stream().map(WeightInfo::getWeightNum).collect(Collectors.toList());
                                if (Collections.max(dataList) - Collections.min(dataList) <= stableRange) {
                                    Integer appearMostNum = queue.stream().map(WeightInfo::getWeightNum)
                                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                            .entrySet()
                                            .stream()
                                            .max((Comparator.comparingLong(Map.Entry<Integer, Long>::getValue)))
                                            .filter(entry -> entry.getValue() > sampledTime / 2)
                                            .map(Map.Entry::getKey)
                                            .orElse(0);
                                    log.debug("appear most weight num: {}", appearMostNum);
                                    if (appearMostNum > minWeight && appearMostNum < maxWeight) {
                                        Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.WEIGHED).setHeader("weight", Double.valueOf(appearMostNum)).build();
                                        weighStateMachine.sendEvent(message);
                                    }
                                }
                                queue.poll();
                                queue.offer(weightInfo);
                            }
                        } else {
                            // 第四位为动态/稳态标志位，0为稳态，1为动态
                            if ((HexUtil.decodeHex(status.toCharArray())[0] & 0b00001000) == 0) {
                                if (queue.size() < sampledTime) {
                                    queue.offer(weightInfo);
                                } else {
                                    List<Integer> dataList = queue.stream().map(WeightInfo::getWeightNum).collect(Collectors.toList());
                                    if (Collections.max(dataList) - Collections.min(dataList) <= stableRange) {
                                        Integer appearMostNum = queue.stream().map(WeightInfo::getWeightNum)
                                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                                .entrySet()
                                                .stream()
                                                .max((Comparator.comparingLong(Map.Entry<Integer, Long>::getValue)))
                                                .filter(entry -> entry.getValue() > sampledTime / 2)
                                                .map(Map.Entry::getKey)
                                                .orElse(0);
                                        log.debug("appear most weight num: {}", appearMostNum);
                                        if (appearMostNum > minWeight && appearMostNum < maxWeight) {
                                            Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.WEIGHED).setHeader("weight", Double.valueOf(appearMostNum)).build();
                                            weighStateMachine.sendEvent(message);
                                        }
                                    }
                                    queue.poll();
                                    queue.offer(weightInfo);
                                }
                            } else {
                                queue.clear();
                            }
                        }
                        log.debug("queue size: {}", queue.size());
                        webSocketHandler.sendJsonToAllUser(rtWeightInfo);
                    }
                }
            } catch (Exception e) {
                log.error("READ SCALE EXCEPTION!!", e);
            }
        };
    }
}
