package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.enums.CompleteStatusEnum;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.domain.RecordPO;
import com.calcifer.weight.entity.domain.TruckInfo;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.RandomService;
import com.calcifer.weight.service.RecordService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.utils.DateUtil;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private TruckInfo truckInfo;

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
            if (!deviceService.getLastModBusDeviceStatus().isInfrared1() || deviceService.getLastModBusDeviceStatus().isInfrared4()) {
                voiceService.voice("系统状态错误，请车辆完全退出后重新进入");
                throw new RuntimeException("status incorrect. " + deviceService.getLastModBusDeviceStatus());
            }
            webSocketHandler.sendWeightLogToAllUser("读卡成功，等待车辆进入...");
            // 道闸打开，红绿灯置为绿
            truckInfo = (TruckInfo) context.getMessageHeader("truckInfo");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_INFO, truckInfo);
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
                List<RecordPO> recordList = recordService.getRecordList(truckInfo.getCarNum(), CompleteStatusEnum.UNCOMPLETED);
                String voice = null;
                if (!recordList.isEmpty()) {
//            if(DateUtil.dateMinDiff(weightMap.get("更新时间")+"",DateUtil.getTime(),"yyyy-MM-dd HH:mm:ss")<10){
//                this.weightStatus = "3";////丢弃意外的二次数据
//                isRedoErr=true;////丢弃意外的二次数据
//                break ;
//            }
                    RecordPO record = recordList.get(0);
                    Double tareWeight = record.getTareWeight() == null ? 0 : record.getTareWeight();
                    RecordPO newRecord = new RecordPO();
                    if (weight > tareWeight) {
                        newRecord.setRoughWeight(weight);
                        newRecord.setTareWeight(tareWeight);
                        newRecord.setNetWeight(weight - tareWeight);
                        newRecord.setRoughWeightTime(DateUtil.getTime());
                    } else {
                        newRecord.setRoughWeight(tareWeight);
                        newRecord.setTareWeight(weight);
                        newRecord.setNetWeight(tareWeight - weight);
                        newRecord.setRoughWeightTime(DateUtil.getTime());
                    }
                    newRecord.setSecondWeighTime(DateUtil.getTime());
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
                    newRecord.setSerialNum(randomService.randomUtils("A1001", DateUtil.getDays()));
                    newRecord.setCarNum(truckInfo.getCarNum());
                    newRecord.setWeighType(String.valueOf(truckInfo.getType()));
                    newRecord.setGoodSender(truckInfo.getFaHuo());
                    newRecord.setGoodReceiver(truckInfo.getShouHuo());
                    newRecord.setGoodName(truckInfo.getGoods());
                    newRecord.setSpecification(truckInfo.getSpec());
                    newRecord.setRoughWeight(0D);
                    newRecord.setRoughWeight(0D);
                    newRecord.setTareWeight(weight);
                    String time = DateUtil.getTime();
                    newRecord.setRoughWeightTime(time);
                    newRecord.setTareWeightTime(time);
                    newRecord.setFirstWeighTime(time);
                    newRecord.setSecondWeighTime(time);
                    if ("一次过磅".equals(truckInfo.getBackup13())) {
                        newRecord.setBak1("2");
                        newRecord.setRecordFinish("1");
                        newRecord.setRoughWeight(weight);
                        newRecord.setTareWeight(0D);
                        newRecord.setNetWeight(weight);
                        newRecord.setBak13("一次过磅");
                    } else {
                        newRecord.setBak1("1");
                        newRecord.setRecordFinish("0");
                        newRecord.setBak13("标准过磅");
                    }
                    newRecord.setBak14("IC卡启用");
                    newRecord.setCustomerType("");
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
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_AND_WEIGHT, "清空页面数据显示");
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
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_AND_WEIGHT, "清空页面数据显示");
            synchronized (AutoScanJob.class) {
                log.info("reset all devices...");
                try {
                    deviceService.destroy();
                } catch (ModbusIOException e) {
                    throw new RuntimeException(e);
                }
                deviceService.init();
            }
        };
    }
}
