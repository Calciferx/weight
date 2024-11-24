package com.calcifer.weight.handler;

import com.calcifer.weight.autoweigh.WeighEventEnum;
import com.calcifer.weight.autoweigh.WeighStatusEnum;
import com.calcifer.weight.entity.domain.CardDO;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CardListener implements SerialPortUtil.DataAvailableListener {
    @Resource
    private StateMachine<WeighStatusEnum, WeighEventEnum> weighStateMachine;
    @Autowired
    private VoiceService voiceService;

    @Autowired
    private CardInfoService cardInfoService;

    @Override
    public void dataAvailable(SerialPortEvent serialPortEvent) {
        try {
            byte[] data = SerialPortUtil.readFromPort(serialPortEvent.getSerialPort());
            String dataHex = Convert.toHex(data).toUpperCase();
            log.info("read card data: {}", dataHex);
            Pattern pattern = Pattern.compile("1100EE00.{28}");
            Matcher matcher = pattern.matcher(dataHex);
            if (matcher.find()) {
                String cardNum = matcher.group().substring(8, 32);
                //                String cardNum = "E2000016660E015616306EDE";
                log.info("read cardNum: {}", cardNum);
                CardDO cardDO = cardInfoService.lambdaQuery().eq(CardDO::getCardNum, cardNum).one();
                if (cardDO != null) {
                    log.info("read truckInfo success");
                    Message<WeighEventEnum> message = MessageBuilder.withPayload(WeighEventEnum.READ_CARD).setHeader("cardInfoDO", cardDO).build();
                    weighStateMachine.sendEvent(message);
                } else {
                    log.error("card not register!!");
                    voiceService.voice("此卡未注册，请联系管理员");
                }
            }
        } catch (Exception e) {
            log.error("READ CARD EXCEPTION!", e);
        }
    }
}
