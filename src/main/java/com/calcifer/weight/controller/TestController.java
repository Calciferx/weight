package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.WeightApplication;
import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.domain.UserSlaveInfo;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.entity.dto.SlaveDetailInfo;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.repository.TestMapper;
import com.calcifer.weight.repository.UserMapper;
import com.calcifer.weight.repository.UserSlaveMapper;
import com.calcifer.weight.service.ModbusDeviceService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.service.WeightPrintService;
import com.calcifer.weight.service.WeightRecordService;
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

    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private ModbusDeviceService modbusDeviceService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @Autowired
    private WeightRecordService weightRecordService;
    @Autowired
    private WeightPrintService printService;

    @RequestMapping("print")
    public Object print(WeightRecordDO recordDO) throws Exception {
        printService.print(recordDO);
        return "over";
    }

    @RequestMapping("stateMachineTest")
    public Object stateMachineTest() {
        weighStateMachine.sendEvent(WeighEventEnum.TRUCK_FOUND);
        return "over";
    }

    @RequestMapping("mptest")
    public Object mpTest() {
        String dhj = weightRecordService.generateWeighId("DHJ");
        return dhj;
    }


    @RequestMapping(value = "wsTest", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object wsTest(@RequestBody String json) {

        webSocketHandler.sendMessageToAllUser(json);
        return json;
    }

    @RequestMapping("closeBarrier")
    public Object closeBarrier() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
        return "closeBarrier";
    }

    @RequestMapping("frontOnFalse")
    public Object frontOnFalse() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
        return "frontOnFalse";
    }

    @RequestMapping("frontOffTrue")
    public Object frontOffTrue() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
        return "frontOffTrue";
    }

    @RequestMapping("frontOffFalse")
    public Object frontOffFalse() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
        return "frontOffFalse";
    }

    @RequestMapping("frontLightFalse")
    public Object frontLightFalse() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getTrafficLight(), false);
        return "frontLightFalse";
    }

    @RequestMapping("frontLightTrue")
    public Object frontLightTrue() {
        modbusDeviceService.controlModBusDevice(WeightContext.front.getTrafficLight(), true);
        return "frontLightTrue";
    }

    @RequestMapping("backLightFalse")
    public Object backLightFalse() {
        modbusDeviceService.controlModBusDevice(WeightContext.back.getTrafficLight(), false);
        return "backLightFalse";
    }

    @RequestMapping("backLightTrue")
    public Object backLightTrue() {
        modbusDeviceService.controlModBusDevice(WeightContext.back.getTrafficLight(), true);
        return "backLightTrue";
    }

//    @RequestMapping("listenerTest")
//    public Object listenerTest() {
//        return deviceService.getCardListener() == null;
//    }

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


    @RequestMapping(value = "/userSlaveInfo/{slaveId}")
    public Object queryUserSlaveInfoTest(@PathVariable("slaveId") String slaveId) {
        List<UserSlaveInfo> userSlaveInfoList = userSlaveMapper.queryUserSlaveInfo(slaveId);
        return new RespWrapper<>(userSlaveInfoList);
    }

    @RequestMapping(value = "/slaveDetail/{slaveId}")
    public Object querySlaveDetailTest(@PathVariable("slaveId") String slaveId) {
        List<SlaveDetailInfo> slaveDetailInfos = null;
        return new RespWrapper<>(slaveDetailInfos);
    }


    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandleTest(Exception e) {
        log.error(e.getMessage(), e);
        return new RespWrapper<>(RespCodeEnum.DEBUG);
    }
}
