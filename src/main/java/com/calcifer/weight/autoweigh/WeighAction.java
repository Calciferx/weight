package com.calcifer.weight.autoweigh;

import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.domain.*;
import com.calcifer.weight.entity.dto.PlateDTO;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.*;
import com.calcifer.weight.utils.PinyinUtil;
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
    @Autowired
    private WeightRecordService weightRecordService;
    @Autowired
    private CarService carService;
    @Autowired
    private SupplierExtInfoService supplierExtInfoService;
    @Autowired
    private WeightPrintService printService;

    private CarDO carDO;
    private WeightInfoDO weightInfoDO;
    private WeightRecordDO weightRecordDO;



    /**
     * source = "WAIT", target = "WAIT_ENTER"
     */
    public Action<WeighStatusEnum, WeighEventEnum> readPlateAction() {
        return context -> {
            log.info("========readPlateAction action========");
            PlateDTO plateDTO = (PlateDTO) context.getMessageHeader("plateDTO");
            webSocketHandler.sendWeightLogToAllUser("发现车辆，读取车牌号: " + plateDTO.getPlateNumber());
            CarDO carDO = carService.lambdaQuery().eq(CarDO::getPlateNumber, plateDTO.getPlateNumber()).one();
            if (carDO == null) {
                voiceService.voice("未读取到该车辆信息");
                throw new RuntimeException("not found plateNumber in database");
            }
            this.carDO = carDO;
            log.info("PlateReader IP: front: {}, back: {}, current: {}", plateDTO.getPlateReaderIP(), WeightContext.front.getPlateReaderIP(), WeightContext.back.getPlateReaderIP());
            // 确定进车方向，红绿灯置为红
            if (!plateDTO.getPlateReaderIP().equals(WeightContext.front.getPlateReaderIP()) && plateDTO.getPlateReaderIP().equals(WeightContext.back.getPlateReaderIP())) {
                log.info("REVERSE DIRECTION!");
                WeightContext.reverseDirection();
            }
            modbusDeviceService.controlTrafficLight(true);
            // 道闸打开，红绿灯置为绿
            TruckInfo truckInfo = new TruckInfo();
            truckInfo.setPlateNumber(plateDTO.getPlateNumber());
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_INFO, truckInfo);
            modbusDeviceService.controlTrafficLight(false);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
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
            // 清空临时存储的刷卡信息
            WeightContext.supplierName = null;
            WeightContext.materialName = null;
            webSocketHandler.sendWeightLogToAllUser("车辆已上称，等待司机刷卡...");
            // 红绿灯置为红，等待司机刷卡
            modbusDeviceService.controlTrafficLight(true);
        };
    }

    /**
     * source = "WAIT_CARD", target = "ON_WEIGH"
     */
    public Action<WeighStatusEnum, WeighEventEnum> readWeightInfo() {
        return context -> {
            log.info("========readWeightInfo action========");
            WeightInfoDO weightInfoDO = (WeightInfoDO) context.getMessageHeader("weightInfoDO");
            weightInfoDO.setMaterialCode(PinyinUtil.getFirstLetters(weightInfoDO.getMaterialName()));
            SupplierExtInfoDO supplierExtInfoDO = supplierExtInfoService.lambdaQuery().eq(SupplierExtInfoDO::getSupplierName, weightInfoDO.getSupplierName()).one();
            if (supplierExtInfoDO != null) {
                weightInfoDO.setIsTest(supplierExtInfoDO.getIsTest());
            } else {
                weightInfoDO.setIsTest("否");
            }
            this.weightInfoDO = weightInfoDO;
            webSocketHandler.sendWeightLogToAllUser("卡片信息读取完成，供应商名称：" + weightInfoDO.getSupplierName()
                    + ",是否化验：" + weightInfoDO.getIsTest()
                    + ",物料名称：" + weightInfoDO.getMaterialName()
                    + ",开始称重");
            voiceService.voice("卡片信息读取完成，供应商名称：" + weightInfoDO.getSupplierName() + ",物料名称：" + weightInfoDO.getMaterialName() + "，开始称重");
            // 红绿灯置为红，开始称重
            modbusDeviceService.controlTrafficLight(true);
            // 称重前清空上次重量信息
            weightRecordDO = null;
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
                WeightRecordDO recordDO = weightRecordService.lambdaQuery().eq(WeightRecordDO::getPlateNumber, carDO.getPlateNumber())
                        .eq(WeightRecordDO::getWeighStatus, "检斤中").one();
                if (recordDO != null) {
                    // 第二次称重
                    Double firstWeight = recordDO.getRoughWeight() == null ? 0 : recordDO.getRoughWeight();
                    Date currentTime = new Date();
                    if (firstWeight > weight) {
                        // 先毛后皮
                        recordDO.setTareWeight(weight);
                        recordDO.setNetWeight(firstWeight - weight);

                        recordDO.setRoughWeightTime(recordDO.getWeighDate());
                        recordDO.setTareWeightTime(currentTime);
                        recordDO.setComment("进厂刘川");
                    } else {
                        // 先皮后毛
                        recordDO.setTareWeight(firstWeight);
                        recordDO.setRoughWeight(weight);
                        recordDO.setNetWeight(weight - firstWeight);

                        recordDO.setTareWeightTime(recordDO.getWeighDate());
                        recordDO.setRoughWeightTime(currentTime);
                        recordDO.setComment("出厂刘川");
                    }
                    recordDO.setWeighDate(currentTime);
                    recordDO.setWeighStatus("检斤完成");

                    weightRecordDO = recordDO;
                    webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.WEIGH_INFO, recordDO);
                    boolean isUpdate = weightRecordService.updateById(recordDO);
                    log.info("update record result: {}", isUpdate);
                    weightRecordDO.setCheckMan("张军");
                    if (!isUpdate) {
                        voiceService.voice("称重失败，请重新上磅计量");
                        webSocketHandler.sendWeightLogToAllUser("称重失败，请重新上磅计量");
                    } else {
                        voiceService.voice("毛重" + recordDO.getRoughWeight() + "皮重" + recordDO.getTareWeight() + "净重"
                                + recordDO.getNetWeight() + ",称重结束，请按按钮打印磅单");
                        log.info("毛重: {}, 皮重: {}, 净重: {}。 称重结束，车辆请下磅", recordDO.getRoughWeight(), recordDO.getTareWeight(), recordDO.getNetWeight());
                        webSocketHandler.sendWeightLogToAllUser("称重完成");
                    }
                } else {
                    // 第一次称重
                    recordDO = new WeightRecordDO();
                    String weighId = weightRecordService.generateWeighId(weightInfoDO.getMaterialCode());
                    log.info("materialCode: {}", weightInfoDO.getMaterialCode());
                    log.info("generate weighId: {}", weighId);
                    recordDO.setWeighId(weighId);
                    recordDO.setMaterialId(weightInfoDO.getMaterialCode());
                    recordDO.setSupplierName(weightInfoDO.getSupplierName());
                    recordDO.setMaterialName(weightInfoDO.getMaterialName());
                    recordDO.setPlateNumber(carDO.getPlateNumber());
                    recordDO.setRoughWeight(weight);
                    recordDO.setWeighDate(new Date());
                    recordDO.setWeighStatus("检斤中");
                    recordDO.setIsPrint("是");
                    recordDO.setControlId(weightInfoDO.getControlId());
                    recordDO.setComment(weightInfoDO.getComment());
                    recordDO.setWeighMan(weightInfoDO.getWeighMan());
                    recordDO.setAuditor(weightInfoDO.getAuditor());
                    recordDO.setIsTest(weightInfoDO.getIsTest());
                    recordDO.setCarType(carDO.getCarType());
                    recordDO.setControlId("Q/CHALCO-GS-910351-JL057-2019");
                    recordDO.setAuditor("梁成超");
                    recordDO.setWeighMan("张杰");

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
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), false);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), false);
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
     * source = "WEIGHED", target = "WEIGHED"
     */
    public Action<WeighStatusEnum, WeighEventEnum> print() {
        return context -> {
            log.info("========print action========");
            if (weightRecordDO != null) {
                try {
                    webSocketHandler.sendWeightLogToAllUser("正在打印...");
                    voiceService.voice("磅单打印中，请稍候");
                    printService.print(weightRecordDO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                voiceService.voice("尚未进行第二次称重，无需打印磅单");
            }
        };
    }


    /**
     * source = "LEAVING_WEIGH", target = "WAIT"
     */
    public Action<WeighStatusEnum, WeighEventEnum> truckLeft() {
        return context -> {
            log.info("========truckLeft action========");
            webSocketHandler.sendWeightLogToAllUser("称重结束，车辆已驶离...");
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.TRUCK_AND_WEIGHT, "清空页面数据显示");
            // 车辆已驶离，道闸关闭，红绿灯置为绿
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
            modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
            modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOff(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOff(), true);
            modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOff(), false);
            modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOff(), false);
            modbusDeviceService.controlTrafficLight(false);
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
