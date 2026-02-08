package com.calcifer.weight.autoweigh;

import cn.hutool.core.date.DatePattern;
import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.po.RecordPO;
import com.calcifer.weight.entity.po.CardInfoPO;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.RandomService;
import com.calcifer.weight.service.RecordService;
import com.calcifer.weight.service.VoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class WeighAction {
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private RandomService randomService;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    private CardInfoPO cardInfoPO;

    /**
     * source = "WAIT", target = "WAIT_CARD"
     */
    public Action<WeighStatusEnum, WeighEventEnum> foundTruck() {
        return context -> {
            log.info("========foundTruck action========");
            webSocketHandler.sendWeightLogToAllUser("发现车辆，等待刷卡...");
            // 确定进车方向，红绿灯置为红
            Boolean isReverse = (Boolean) context.getMessageHeader("reverse");
            if (isReverse == null) {
                log.error("isReverse is null!");
                throw new RuntimeException("isReverse is null!");
            }
            deviceService.reverseDirection(isReverse);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, true);
        };
    }

    /**
     * source = "WAIT_CARD", target = "WAIT_ENTER"
     */
    public Action<WeighStatusEnum, WeighEventEnum> waitTruckEntering() {
        return context -> {
            log.info("========waitTruckEntering action========");
            if (!deviceService.getLastModBusDeviceStatus().isInfraredFrontFound() || deviceService.getLastModBusDeviceStatus().isInfraredBackFound()) {
                voiceService.voice("系统状态错误，请车辆完全退出后重新进入");
                throw new RuntimeException("status incorrect. " + deviceService.getLastModBusDeviceStatus());
            }
            webSocketHandler.sendWeightLogToAllUser("读卡成功，等待车辆进入...");
            // 道闸打开，红绿灯置为绿
            cardInfoPO = (CardInfoPO) context.getMessageHeader("truckInfo");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_INFO, cardInfoPO);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
            voiceService.voice("读卡成功，车辆请上称");
        };
    }

    /**
     * source = "WAIT_CARD", target = "WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeave() {
        return context -> {
            log.info("========truckLeave action========");
            webSocketHandler.sendWeightLogToAllUser("车辆未刷卡，离开...");
            // 红绿灯置为绿
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
        };
    }

    /**
     * source = "WAIT_ENTER", target = "ENTERING"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckEntering() {
        return context -> {
            log.info("========truckEntering action========");
            webSocketHandler.sendWeightLogToAllUser("车辆正在上称...");
            // 无需动作，等待车辆上称完成
        };
    }

    /**
     * source = "ENTERING", target = "ON_WEIGH"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckEntered() {
        return context -> {
            log.info("========truckEntered action========");
            webSocketHandler.sendWeightLogToAllUser("车辆已上称，开始称重...");
            // 道闸关闭，红绿灯置为红，开始称重
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
//            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
//            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        };
    }

    /**
     * source = "ON_WEIGH", target = "WEIGHED"
     */
    public Action<WeighStatusEnum, WeighEventEnum> weigh() {
        return new Action<WeighStatusEnum, WeighEventEnum>() {
            @Transactional
            @Override
            public void execute(StateContext<WeighStatusEnum, WeighEventEnum> context) {
                log.info("========weigh action========");
                // 称重完毕，记录重量，道闸打开，车辆驶离
                Double weight = (Double) context.getMessageHeader("weight");
                log.info("***** weight is: {} *****", weight);
                List<RecordPO> recordList = recordService.getRecordList(cardInfoPO.getCarNum(), CompleteStatusEnum.UNCOMPLETED);
                String voice = null;
                Date now = new Date();
                if (!recordList.isEmpty()) {
                    RecordPO record = recordList.get(0);
                    Double tareWeight = record.getTareWeight() == null ? 0 : record.getTareWeight();
                    RecordPO newRecord = new RecordPO();
                    if (weight > tareWeight) {
                        newRecord.setGrossWeight(weight);
                        newRecord.setTareWeight(tareWeight);
                        newRecord.setNetWeight(weight - tareWeight);
                        newRecord.setRoughWeightTime(now);
                    } else {
                        newRecord.setGrossWeight(tareWeight);
                        newRecord.setTareWeight(weight);
                        newRecord.setNetWeight(tareWeight - weight);
                        newRecord.setTareWeightTime(now);
                    }
                    newRecord.setSecondWeighTime(now);
                    newRecord.setSecondWeight(weight);
                    newRecord.setBak1("2");
                    newRecord.setSerialNum(record.getSerialNum());

                    webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, newRecord);
                    int updateRowNum = recordService.updateRecord(newRecord);
                    log.info("update record num: {}", updateRowNum);
                    if (updateRowNum == 0) {
                        voice = "称重失败，请重新上磅计量";
                        webSocketHandler.sendWeightLogToAllUser("称重失败，请重新上磅计量");
                    } else {
                        voice = "毛重" + weight + "皮重" + record.getTareWeight() + "净重"
                                + newRecord.getNetWeight() + ",称重结束，车辆请下磅";
                        log.info("毛重: {}, 皮重: {}, 净重: {}。 称重结束，车辆请下磅", weight, record.getTareWeight(), newRecord.getNetWeight());
                        webSocketHandler.sendWeightLogToAllUser("称重完成");
                    }
                    webSocketHandler.sendWeightLogToAllUser("#后道闸已打开，车辆请离场");
                } else {
                    RecordPO newRecord = new RecordPO();
                    //TODO randomService优化
                    newRecord.setSerialNum(randomService.randomUtils("A1001", DatePattern.PURE_DATE_FORMAT.format(now)));
                    newRecord.setCarNo(cardInfoPO.getCarNum());
                    newRecord.setWeighType(String.valueOf(cardInfoPO.getType()));
                    newRecord.setGoodsSender(cardInfoPO.getFaHuo());
                    newRecord.setGoodsReceiver(cardInfoPO.getShouHuo());
                    newRecord.setGoodsName(cardInfoPO.getGoods());
                    newRecord.setSpecification(cardInfoPO.getSpec());
                    newRecord.setGrossWeight(0D);
                    newRecord.setGrossWeight(0D);
                    newRecord.setTareWeight(weight);
                    newRecord.setRoughWeightTime(now);
                    newRecord.setTareWeightTime(now);
                    newRecord.setFirstWeighTime(now);
                    newRecord.setSecondWeighTime(now);
                    if ("一次过磅".equals(cardInfoPO.getBackup13())) {
                        newRecord.setBak1("2");
                        newRecord.setRecordFinish(1);
                        newRecord.setGrossWeight(weight);
                        newRecord.setTareWeight(0D);
                        newRecord.setNetWeight(weight);
                        newRecord.setBak13("一次过磅");
                    } else {
                        newRecord.setBak1("1");
                        newRecord.setRecordFinish(0);
                        newRecord.setBak13("标准过磅");
                    }
                    newRecord.setBak14("IC卡启用");
                    newRecord.setCustomerType(0);
                    newRecord.setFirstWeight(weight);
                    newRecord.setSecondWeight(0D);
                    int addRowNum = recordService.addRecord(newRecord);
                    if (addRowNum != 1) {
                        log.info("add record failed");
                        voice = "称重失败，请重新上磅计量";
                        webSocketHandler.sendWeightLogToAllUser("称重失败，请重新上磅计量");
                    } else {
                        voice = "重量" + weight + "称重结束，车辆请下磅";
                        webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, newRecord);
                        webSocketHandler.sendWeightLogToAllUser("称重完成");
                        webSocketHandler.sendWeightLogToAllUser("#后道闸已打开，车辆请下磅、离场");
                    }
                }
                deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, true);
                deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, true);
                deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
                deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
                voiceService.voice(voice);
            }
        };
    }

    /**
     * source = "WEIGHED", target = "LEAVING_WEIGH"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeavingWeigh() {
        return context -> {
            log.info("========truckLeavingWeigh action========");
            webSocketHandler.sendWeightLogToAllUser("车辆正在下称...");
            // 车辆正在下称
        };
    }

    /**
     * source = "LEAVING_WEIGH", target = "LEFT_WEIGH"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeftWeigh() {
        return context -> {
            log.info("========truckLeftWeigh action========");
            webSocketHandler.sendWeightLogToAllUser("车辆已下称...");
            // 车辆已下称
        };

    }

    /**
     * source = "LEFT_WEIGH", target = "LEAVING"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeaving() {
        return context -> {
            log.info("========truckLeaving action========");
            webSocketHandler.sendWeightLogToAllUser("车辆正在驶离...");
            // 车辆正在驶离
            // 驶离时暂停响应，避免将拖挂车中间缝误判为已经离场
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

    }

    /**
     * source = "LEAVING", target = "WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeft() {
        return context -> {
            log.info("========truckLeft action========");
            webSocketHandler.sendWeightLogToAllUser("称重结束，车辆已驶离...");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, new RecordPO());
            // 车辆已驶离，道闸关闭，红绿灯置为绿
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            deviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
            voiceService.voice("称重结束，车辆已驶离");
        };
    }

    public Action<WeighStatusEnum, WeighEventEnum> reset() {
        return context -> {
            log.info("========reset action========");
            // 清空
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, new RecordPO());
//            synchronized (AutoScanJob.class) {
            log.info("reset all devices...");
            try {
                deviceService.destroy();
                deviceService.init();
            } catch (Exception e) {
                // 将异常信息存入 ExtendedState
                context.getExtendedState().getVariables().put("ERROR_MSG", "硬件重置失败: " + e.getMessage());
                // 抛出异常或设置错误标志，使 stateMachine.hasStateMachineError() 返回 true
                throw new RuntimeException(e);
            }
//            }
        };
    }

    /**
     * source = "WAIT", target = "STOP_WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> stopWait() {
        return context -> {
            log.info("========stopWait action========");
            webSocketHandler.sendWeightLogToAllUser("***停止自动过磅***");
        };
    }

    /**
     * source = "STOP_WAIT", target = "WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> startWait() {
        return context -> {
            log.info("========startWait action========");
            webSocketHandler.sendWeightLogToAllUser("***开始自动过磅***");
        };
    }

    /**
     * 进入WAIT状态时执行
     * 未进入计量流程时允许停止自动过磅，发消息启用前端按钮
     */
    public Action<WeighStatusEnum, WeighEventEnum> waitEntry() {
        return context -> {
            log.info("========waitEntry action========");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.AUTO_WEIGHT_BUTTON, true);
        };
    }

    /**
     * 进入WAIT_CARD状态时执行
     * 进入计量流程后不允许停止自动过磅，发消息禁用前端按钮
     */
    public Action<WeighStatusEnum, WeighEventEnum> waitCardEntry() {
        return context -> {
            log.info("========waitCardEntry action========");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.AUTO_WEIGHT_BUTTON, false);
        };
    }
}
