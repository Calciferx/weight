package com.calcifer.weight.autoweigh;

import com.calcifer.weight.entity.domain.TruckInfo;
import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.service.DeviceService;
import com.calcifer.weight.service.RandomService;
import com.calcifer.weight.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine(name = "weighStateMachine")
@Slf4j
public class WeighStateListener {
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private RandomService randomService;

    @Autowired
    private WeightWebSocketHandler webSocketHandler;

    private TruckInfo truckInfo;

    @OnTransition(source = "WAIT", target = "WAIT_CARD")
    public void readCard(Message<WeighStatusEnum> message) {
        // 确定进车方向，红绿灯置为红
        log.info("state machine transition. from: {}, to: {}", "WAIT", "WAIT_CARD");
    }

    @OnTransition(source = "WAIT_CARD", target = "WAIT_ENTER")
    public void waitTruckEntering(Message<WeighStatusEnum> message) {
        // 道闸打开，红绿灯置为绿
        log.info("state machine transition. from: {}, to: {}", "WAIT_CARD", "WAIT_ENTER");
    }

    @OnTransition(source = "WAIT_CARD", target = "WAIT")
    public void truckLeave(Message<WeighStatusEnum> message) {
        // 红绿灯置为绿
        log.info("state machine transition. from: {}, to: {}", "WAIT_CARD", "WAIT");
    }

    @OnTransition(source = "WAIT_ENTER", target = "ENTERING")
    public void truckEntering(Message<WeighStatusEnum> message) {
        // 无需动作，等待车辆上称完成
        log.info("state machine transition. from: {}, to: {}", "WAIT_ENTER", "ENTERING");
    }

    @OnTransition(source = "ENTERING", target = "ON_WEIGH")
    public void truckEntered(Message<WeighStatusEnum> message) {
        // 道闸关闭，红绿灯置为红，开始称重
        log.info("state machine transition. from: {}, to: {}", "ENTERING", "ON_WEIGH");
    }

    @OnTransition(source = "ON_WEIGH", target = "WEIGHED")
    public void weigh(Message<WeighStatusEnum> message) {
        // 称重完毕，记录重量，道闸打开，车辆驶离
        log.info("state machine transition. from: {}, to: {}", "ON_WEIGH", "WEIGHED");
    }

    @OnTransition(source = "WEIGHED", target = "LEAVING_WEIGH")
    public void truckLeavingWeigh(Message<WeighStatusEnum> message) {
        // 车辆正在下称
        log.info("state machine transition. from: {}, to: {}", "WEIGHED", "LEAVING_WEIGH");
    }

    @OnTransition(source = "LEAVING_WEIGH", target = "LEFT_WEIGH")
    public void truckLeftWeigh(Message<WeighStatusEnum> message) {
        // 车辆已下称
        log.info("state machine transition. from: {}, to: {}", "LEAVING_WEIGH", "LEFT_WEIGH");
    }

    @OnTransition(source = "LEFT_WEIGH", target = "LEAVING")
    public void truckLeaving(Message<WeighStatusEnum> message) {
        // 车辆正在驶离
        log.info("state machine transition. from: {}, to: {}", "LEFT_WEIGH", "LEAVING");
    }


    @OnTransition(source = "LEAVING", target = "WAIT")
    public void truckLeft(Message<WeighStatusEnum> message) {
        // 车辆已驶离，道闸关闭，红绿灯置为绿
        log.info("state machine transition. from: {}, to: {}", "LEAVING", "WAIT");
    }
}
