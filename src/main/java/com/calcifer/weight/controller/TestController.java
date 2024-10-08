package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.WeightApplication;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.calcifer.weight.entity.po.UserPO;
import com.calcifer.weight.entity.po.UserSlaveInfo;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.*;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.VoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private TestMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSlaveMapper userSlaveMapper;

    @Autowired
    private SlaveDetailMapper slaveDetailMapper;

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

    @RequestMapping("listenerTest")
    public Object listenerTest() {
        return deviceService.getCardListener() == null;
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
        return cardMapper.getTruckInfo(cardNum);
    }

    @RequestMapping(value = "/hello")
    public String testMethod() {
        List<HashMap<String, Object>> hashMaps = mapper.queryTest();
        return JSON.toJSONString(hashMaps);
    }

    @RequestMapping(value = "/ex")
    public String exceptionHandleTest() {
        List<HashMap<String, Object>> hashMaps = mapper.queryTest();
        throw new RuntimeException("hello异常");
//        return JSON.toJSONString(hashMaps);
    }

    @RequestMapping(value = "/world/{name}")
    public Object queryUserTest(@PathVariable("name") String name) {
        UserPO admin = userMapper.queryUser(new UserPO(name));
        return new RespWrapper<>(admin, RespCodeEnum.SUCCESS, (admin.getStatus() == UserStatusEnum.FORBIDDEN) + "");
    }

    @RequestMapping(value = "/userSlaveInfo/{slaveId}")
    public Object queryUserSlaveInfoTest(@PathVariable("slaveId") String slaveId) {
        List<UserSlaveInfo> userSlaveInfoList = userSlaveMapper.queryUserSlaveInfo(slaveId);
        return new RespWrapper<>(userSlaveInfoList);
    }

    @RequestMapping(value = "/slaveDetail/{slaveId}")
    public Object querySlaveDetailTest(@PathVariable("slaveId") String slaveId) {
        SlaveDetailInfo slaveDetailInfo = new SlaveDetailInfo(slaveId, null, null);
        List<SlaveDetailInfo> slaveDetailInfos = slaveDetailMapper.querySlaveDetailInfo(slaveDetailInfo);
        return new RespWrapper<>(slaveDetailInfos);
    }


    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
