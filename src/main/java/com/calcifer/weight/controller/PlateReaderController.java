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

    /**
     * {
     * "AlarmInfoPlate": {
     * "channel": 0,
     * "deviceName": "IVS",
     * "ipaddr": "192.168.1.100",
     * "result": {
     * "PlateResult": {
     * "clean_time": 0,
     * "colorType": 1,
     * "direction": 0,
     * "gioouts": [
     * {
     * "ctrltype": 0,
     * "ionum": "0"
     * },
     * {
     * "ctrltype": 0,
     * "ionum": "1"
     * },
     * {
     * "ctrltype": 0,
     * "ionum": "2"
     * }
     * ],
     * "isoffline": 0,
     * "license": "鲁LFM200",
     * "license_ext_type": 0,
     * "plate_true_width": 475,
     * "plateid": 1976,
     * "plates": [
     * {
     * "color": 1,
     * "is_danger": -1,
     * "license": "鲁LFM200",
     * "plate_width": 475,
     * "pos": {
     * "bottom": 1192,
     * "left": 1107,
     * "right": 1581,
     * "top": 1007
     * },
     * "type": 1
     * }
     * ],
     * "triggerType": 8,
     * "type": 1
     * }
     * },
     * "rule_id": 0,
     * "serialno": "a516a283-48aa53e3",
     * "user_data": ""
     * }
     * }
     */
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
