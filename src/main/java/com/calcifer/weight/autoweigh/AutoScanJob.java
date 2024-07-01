package com.calcifer.weight.autoweigh;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.dto.WeightInfo;
import com.calcifer.weight.entity.po.TruckInfo;
import com.calcifer.weight.entity.vo.WeightInfoVO;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.CardMapper;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.utils.SerialPortUtil;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.xiaoleilu.hutool.convert.Convert;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
                        WeightInfo weightInfo = new WeightInfo(status, dataHex, num);
                        WeightInfoVO weightInfoVO = new WeightInfoVO(num, "7", weightInfo);
                        log.debug("weight map: {}", JSON.toJSONString(weightInfoVO));
                        if (queue.size() < sampledTime) {
                            queue.offer(weightInfo);
                        } else {
                            List<Integer> dataList = queue.stream().map(WeightInfo::getWeightNum).collect(Collectors.toList());
                            if (Collections.max(dataList) - Collections.min(dataList) < 10) {
                                Double average = dataList.stream().mapToInt(Integer::intValue).average().orElse(0D);
                                log.debug("average weight: {}", average);
                                if (average > minWeight && average < maxWeight) {
                                    Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.WEIGHED).setHeader("weight", average).build();
                                    weighStateMachine.sendEvent(message);
                                }
                            }
                            queue.poll();
                            queue.offer(weightInfo);
                        }
                        log.debug("queue size: {}", queue.size());
                        webSocketHandler.sendJsonToAllUser(weightInfoVO);
                    }
                }
            } catch (Exception e) {
                log.error("READ SCALE EXCEPTION!!", e);
            }
        };
    }
}
