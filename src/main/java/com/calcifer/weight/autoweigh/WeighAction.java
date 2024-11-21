package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.domain.CarInfoDO;
import com.calcifer.weight.entity.domain.TruckInfo;
import com.calcifer.weight.entity.domain.WeightInfoDO;
import com.calcifer.weight.entity.domain.WeightRecordDO;
import com.calcifer.weight.entity.enums.ModBusDeviceEnum;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.ModbusDeviceService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.service.WeightRecordService;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component
public class WeighAction {
    @Autowired
    private ModbusDeviceService modbusDeviceService;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    private CarInfoDO carInfoDO;

    private WeightInfoDO weightInfoDO;

    @Autowired
    private WeightRecordService weightRecordService;

    /**
     * source = "WAIT", target = "READ_PLATE_NUM"
     */
    public Action<WeighStatusEnum, WeighEventEnum> foundTruck() {
        return context -> {
            log.info("========foundTruck action========");
            webSocketHandler.sendWeightLogToAllUser("发现车辆，读取车牌号...");
            // 确定进车方向，红绿灯置为红
            Boolean isReverse = (Boolean) context.getMessageHeader("reverse");
            if (isReverse == null) {
                log.error("isReverse is null!");
                throw new RuntimeException("isReverse is null!");
            }
            modbusDeviceService.reverseDirection(isReverse);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, true);
        };
    }

    /**
     * source = "READING_PLATE_NUM", target = "WAIT_ENTER"
     */
    public Action<WeighStatusEnum, WeighEventEnum> waitTruckEntering() {
        return context -> {
            log.info("========waitTruckEntering action========");
            if (!modbusDeviceService.getLastModBusDeviceStatus().isInfrared1() || modbusDeviceService.getLastModBusDeviceStatus().isInfrared4()) {
                voiceService.voice("系统状态错误，请车辆完全退出后重新进入");
                throw new RuntimeException("status incorrect. " + modbusDeviceService.getLastModBusDeviceStatus());
            }
            webSocketHandler.sendWeightLogToAllUser("车牌号读取成功，等待车辆进入...");
            // 道闸打开，红绿灯置为绿
            TruckInfo truckInfo = (TruckInfo) context.getMessageHeader("truckInfo");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_INFO, truckInfo);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_ON, false);
            voiceService.voice("读卡成功，车辆请上称");
        };
    }

    /**
     * source = "READING_PLATE_NUM", target = "WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeave() {
        return context -> {
            log.info("========truckLeave action========");
            webSocketHandler.sendWeightLogToAllUser("车辆驶离，停止识别车牌号...");
            // 红绿灯置为绿
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
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
     * source = "ENTERING", target = "WAIT_CARD"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckEntered() {
        return context -> {
            log.info("========truckEntered action========");
            webSocketHandler.sendWeightLogToAllUser("车辆已上称，等待司机刷卡...");
            // 道闸关闭，红绿灯置为红，开始称重
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
//            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
//            deviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
        };
    }

    /**
     * source = "WAIT_CARD", target = "ON_WEIGH"
     */
    public Action<WeighStatusEnum, WeighEventEnum> readWeightInfo() {
        return context -> {
            log.info("========readWeightInfo action========");
            webSocketHandler.sendWeightLogToAllUser("刷卡成功，读取预置称重信息...");
            // 道闸关闭，红绿灯置为红，开始称重
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, true);
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
                WeightRecordDO recordDO = weightRecordService.lambdaQuery().eq(WeightRecordDO::getPlateNumber, carInfoDO.getPlateNumber())
                        .eq(WeightRecordDO::getWeighStatus, "毛重已检").one();
                if (recordDO != null) {
                    Double firstWeight = recordDO.getTareWeight();
                    Double roughWeight = firstWeight > weight ? firstWeight : weight;
                    Double tareWeight = firstWeight < weight ? firstWeight : weight;
                    Double netWeight = roughWeight - tareWeight;
                    recordDO.setRoughWeight(roughWeight);
                    recordDO.setTareWeight(tareWeight);
                    recordDO.setNetWeight(netWeight);

                    webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, recordDO);
                    boolean isUpdate = weightRecordService.updateById(recordDO);
                    log.info("update record result: {}", isUpdate);
                    if (!isUpdate) {
                        voiceService.voice("称重失败，请重新上磅计量");
                        webSocketHandler.sendWeightLogToAllUser("称重失败，请重新上磅计量");
                    } else {
                        voiceService.voice("毛重" + weight + "皮重" + tareWeight + "净重"
                                + netWeight + ",称重结束，车辆请下磅");
                        log.info("毛重: {}, 皮重: {}, 净重: {}。 称重结束，车辆请下磅", weight, tareWeight, netWeight);
                        webSocketHandler.sendWeightLogToAllUser("称重完成");
                    }
                } else {
                    recordDO = new WeightRecordDO();
                    recordDO.setWeighId(weightRecordService.generateWeighId(weightInfoDO.getMaterialCode()));
                    recordDO.setSupplierName(weightInfoDO.getSupplierName());
                    recordDO.setMaterialName(weightInfoDO.getMaterialName());
                    recordDO.setPlateNumber(carInfoDO.getPlateNumber());
                    recordDO.setRoughWeight(weight);
                    recordDO.setWeighDate(new Date());
                    recordDO.setWeighStatus("毛重已检");
                    recordDO.setIsPrint("是");
                    recordDO.setControlId(weightInfoDO.getControlId());
                    recordDO.setComment(weightInfoDO.getComment());
                    recordDO.setWeighMan(weightInfoDO.getWeighMan());
                    recordDO.setAuditor(weightInfoDO.getAuditor());
                    recordDO.setIsTest(weightInfoDO.getIsTest());
                    recordDO.setCarType(carInfoDO.getCarType());

                    boolean isSave = weightRecordService.save(recordDO);
                    if (!isSave) {
                        log.info("add record failed");
                        voiceService.voice("称重失败，请重新上磅计量");
                        webSocketHandler.sendWeightLogToAllUser("称重失败，请重新上磅计量");
                    } else {
                        voiceService.voice("重量" + weight + "称重结束，车辆请下磅");
                        webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, recordDO);
                        webSocketHandler.sendWeightLogToAllUser("称重完成");
                    }
                }
                modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, true);
                modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, true);
                modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
                modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_ON, false);
                webSocketHandler.sendWeightLogToAllUser("#后道闸已打开，车辆请离场");
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
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_BARRIER_OFF, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, true);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_BARRIER_OFF, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.FRONT_LIGHT, false);
            modbusDeviceService.controlModBusDevice(ModBusDeviceEnum.BACK_LIGHT, false);
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
                    modbusDeviceService.destroy();
                } catch (ModbusIOException e) {
                    throw new RuntimeException(e);
                }
                modbusDeviceService.init();
            }
        };
    }
}
