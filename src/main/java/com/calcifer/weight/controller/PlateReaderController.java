package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.dto.PlateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("plate")
public class PlateReaderController {
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @RequestMapping("result")
    public Object getPlateResult(@RequestBody String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        log.info(json);
        JSONObject jo = JSON.parseObject(json);
        String plateNumber = jo.getJSONObject("AlarmInfoPlate").getJSONObject("result").getJSONObject("PlateResult").getString("license");
        String plateReaderIP = jo.getJSONObject("AlarmInfoPlate").getString("ipaddr");
        return "";
    }

    @RequestMapping("readPlate")
    public Object readPlate(@RequestBody String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        log.info(json);
        JSONObject jo = JSON.parseObject(json);
        String plateNumber = jo.getJSONObject("AlarmInfoPlate").getJSONObject("result").getJSONObject("PlateResult").getString("license");
        String plateReaderIP = jo.getJSONObject("AlarmInfoPlate").getString("ipaddr");
        WeightContext.lastStatusChange = System.currentTimeMillis();
        PlateDTO plateDTO = new PlateDTO(plateNumber, plateReaderIP);
        Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.READ_PLATE_NUM).setHeader("plateDTO", plateDTO).build();
        weighStateMachine.sendEvent(message);
        return "read plate number success";
    }
}
