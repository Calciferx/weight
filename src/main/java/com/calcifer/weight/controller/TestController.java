package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.calcifer.weight.WeightApplication;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.po.CardInfoPO;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.CardMapper;
import com.calcifer.weight.repository.UserMapper;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.SlaveDetailService;
import com.calcifer.weight.service.VoiceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SlaveDetailService slaveDetailService;

    @Autowired
    private CardMapper cardMapper;

    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;


    @RequestMapping(value = "wsTest", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object wsTest(@RequestBody String json) {
        webSocketHandler.sendMessageToAllUser(json);
        return json;
    }

    @RequestMapping("closeBarrier")
    public Object closeBarrier() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        return "closeBarrier";
    }

    @RequestMapping("frontOnFalse")
    public Object frontOnFalse() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
        return "frontOnFalse";
    }

    @RequestMapping("frontOffTrue")
    public Object frontOffTrue() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
        return "frontOffTrue";
    }

    @RequestMapping("frontOffFalse")
    public Object frontOffFalse() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        return "frontOffFalse";
    }

    @RequestMapping("frontLightFalse")
    public Object frontLightFalse() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
        return "frontLightFalse";
    }

    @RequestMapping("frontLightTrue")
    public Object frontLightTrue() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
        return "frontLightTrue";
    }

    @RequestMapping("backLightFalse")
    public Object backLightFalse() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
        return "backLightFalse";
    }

    @RequestMapping("backLightTrue")
    public Object backLightTrue() {
        deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, true);
        return "backLightTrue";
    }


    @RequestMapping("jarPathTest")
    public Object jarPathTest() throws URISyntaxException {
//        String path1 = new File(WeightApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        String path2 = WeightApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String path3 = new File(WeightApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();

        return path2 + "\n" + path3;
    }

    @RequestMapping("voiceTest")
    public Object voiceTest(String content) throws IOException {
        voiceService.voice(content);
        return "over";
    }

    @RequestMapping("stateMachineExTest")
    public Object stateMachineExTest() {
        Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.TRUCK_FOUND).build();
        weighStateMachine.sendEvent(message);
        return "OK";
    }

    @RequestMapping("cardMapperTest/{cardNum}")
    public Object getTruckInfo(@PathVariable("cardNum") String cardNum) {
        return cardMapper.selectOne(new LambdaQueryWrapper<CardInfoPO>().eq(CardInfoPO::getCardNum, cardNum));
    }

    @RequestMapping(value = "/hello")
    public String testMethod() {
        List<Map<String, Object>> hashMaps = userMapper.selectMaps(null);
        return JSON.toJSONString(hashMaps);
    }

    @RequestMapping(value = "/ex")
    public String exceptionHandleTest() {
        List<Map<String, Object>> hashMaps = userMapper.selectMaps(null);
        throw new RuntimeException("hello异常");
//        return JSON.toJSONString(hashMaps);
    }

    @RequestMapping(value = "/world/{name}")
    public Object queryUserTest(@PathVariable("name") String name) {
        UserPO admin = userMapper.selectOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, name));
        return new RespWrapper<>(admin);
    }

    @RequestMapping(value = "/slaveDetail/{slaveId}")
    public Object querySlaveDetailTest(@PathVariable("slaveId") String slaveId) {
        List<SlaveDetailInfo> slaveDetailInfos = slaveDetailService.querySlaveDetailInfoBySlaveId(slaveId);
        return new RespWrapper<>(slaveDetailInfos);
    }
}
