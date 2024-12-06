package com.calcifer.weight.handler;

import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.domain.CardDO;
import com.calcifer.weight.entity.domain.WeightInfoDO;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.service.CardInfoService;
import com.calcifer.weight.service.VoiceService;
import com.calcifer.weight.utils.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.xiaoleilu.hutool.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CardListener implements SerialPortUtil.DataAvailableListener {
    public static final String DATA_TYPE_SUPPLIER = "供应商";
    public static final String DATA_TYPE_MATERIAL = "物料";
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;
    @Autowired
    private VoiceService voiceService;

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    @Override
    public void dataAvailable(SerialPortEvent serialPortEvent) {
        try {
            byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
            if (data == null) {
                log.error("serial data is null!");
                return;
            }
            String dataHex = Convert.toHex(data).toUpperCase();
            log.info("read card data: {}", dataHex);
            webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.READ_CARD_NUM, dataHex);
            if (weighStateMachine.getState().getId() != WeighStatusEnum.WAIT_CARD)
                return;
            CardDO cardDO = cardInfoService.lambdaQuery().eq(CardDO::getCardNum, dataHex).one();
            if (cardDO != null) {
                log.info("read card info success");
                if (cardDO.getDataType().equals(DATA_TYPE_SUPPLIER)) {
                    WeightContext.supplierName = cardDO.getData();
                    webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.READ_SUPPLIER_NAME, WeightContext.supplierName);
                    voiceService.voice("供应商名称:" + WeightContext.supplierName);
                }
                if (cardDO.getDataType().equals(DATA_TYPE_MATERIAL)) {
                    WeightContext.materialName = cardDO.getData();
                    webSocketHandler.sendWSJsonToAllUser(WSCodeEnum.READ_MATERIAL_NAME, WeightContext.materialName);
                    voiceService.voice("物料名称:" + WeightContext.materialName);
                }
                if (WeightContext.supplierName != null && WeightContext.materialName != null) {
                    WeightInfoDO weightInfoDO = new WeightInfoDO();
                    weightInfoDO.setMaterialName(WeightContext.materialName);
                    weightInfoDO.setSupplierName(WeightContext.supplierName);
                    Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.READ_CARD).setHeader("weightInfoDO", weightInfoDO).build();
                    weighStateMachine.sendEvent(message);
                }
            } else {
                log.error("card not register!!");
                voiceService.voice("此卡未注册，请联系管理员");
            }
        } catch (Exception e) {
            log.error("READ CARD EXCEPTION!", e);
        }
    }
}
