package com.calcifer.weight.handler;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.dto.WeightInfo;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.calcifer.weight.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.xiaoleilu.hutool.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScaleListener implements SerialPortUtil.DataAvailableListener {
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;
    @Autowired
    private WeightWebSocketHandler webSocketHandler;
    @Value("${calcifer.weight.min-weight:1000}")
    private Double minWeight;
    @Value("${calcifer.weight.max-weight:100000}")
    private Double maxWeight;
    @Value("${calcifer.weight.sampled-time:30}")
    private int sampledTime;
    private final Queue<WeightInfo> queue = new LinkedList<>();

    private final Queue<Byte> byteQueue = new LinkedBlockingQueue<>(18);

    @Override
    public void dataAvailable(SerialPortEvent serialPortEvent) {
        try {
            byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
            if (data == null) {
                log.error("scale data is null");
                return;
            }
            for (byte aByte : data) {
                if (!byteQueue.offer(aByte)) {
                    String dataHex = Convert.toHex(ArrayUtils.toPrimitive(byteQueue.toArray(new Byte[0]))).toUpperCase();
                    if (dataHex.endsWith("0D0A")) {
                        log.debug("read scale data: {}", dataHex);
                        processScaleData(dataHex);
                        byteQueue.clear();
                    } else {
                        byteQueue.poll();
                    }
                    byteQueue.offer(aByte);
                }
            }
        } catch (Exception e) {
            log.error("READ SCALE EXCEPTION!!", e);
        }
    }

    private void processScaleData(String validHex) {
        // 提取重量信息
        String numHex = validHex.substring(16, 28);
        String numStr = Convert.hexStrToStr(numHex, StandardCharsets.US_ASCII).trim();
        String status = validHex.substring(0, 4);
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
            if ("5354".equals(status)) {
                if (queue.size() < sampledTime) {
                    queue.offer(weightInfo);
                } else {
                    List<Integer> dataList = queue.stream().map(WeightInfo::getWeightNum).collect(Collectors.toList());
                    if (Collections.max(dataList) - Collections.min(dataList) < 10) {
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
            log.debug("queue size: {}", queue.size());
            webSocketHandler.sendJsonToAllUser(rtWeightInfo);
        }
    }
}
