package com.calcifer.weight.autoweigh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine(name = {"weighStateMachine"})
public class WeighStateMachineConfig extends StateMachineConfigurerAdapter<WeighStatusEnum, WeighEventEnum> {
    @Autowired
    private WeighAction weighAction;

    public void configure(StateMachineConfigurationConfigurer<WeighStatusEnum, WeighEventEnum> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true);
    }

    /**
     * 配置状态
     */
    public void configure(StateMachineStateConfigurer<WeighStatusEnum, WeighEventEnum> states) throws Exception {
        states
                .withStates()
                .initial(WeighStatusEnum.WAIT)
                .states(EnumSet.allOf(WeighStatusEnum.class))
                .state(WeighStatusEnum.WAIT, weighAction.waitEntry(), null)
                .state(WeighStatusEnum.TRUCK_FOUND, weighAction.waitCardEntry(), null);
    }

    /**
     * 配置状态转换事件关系
     */
    public void configure(StateMachineTransitionConfigurer<WeighStatusEnum, WeighEventEnum> transitions) throws Exception {
        transitions
                .withExternal().source(WeighStatusEnum.WAIT).target(WeighStatusEnum.TRUCK_FOUND).event(WeighEventEnum.TRUCK_FOUND).action(weighAction.foundTruck())
                .and().withExternal().source(WeighStatusEnum.TRUCK_FOUND).target(WeighStatusEnum.CARD_READ).event(WeighEventEnum.READ_CARD).action(weighAction.waitTruckEntering())
                .and().withExternal().source(WeighStatusEnum.TRUCK_FOUND).target(WeighStatusEnum.WAIT).event(WeighEventEnum.CANCEL_ENTER).action(weighAction.truckLeave())
                .and().withExternal().source(WeighStatusEnum.CARD_READ).target(WeighStatusEnum.ENTERING).event(WeighEventEnum.ENTER).action(weighAction.truckEntering())
                .and().withExternal().source(WeighStatusEnum.ENTERING).target(WeighStatusEnum.WEIGHING).event(WeighEventEnum.ENTERED).action(weighAction.truckEntered())
                .and().withExternal().source(WeighStatusEnum.WEIGHING).target(WeighStatusEnum.WEIGHED).event(WeighEventEnum.WEIGHED).action(weighAction.weigh())
                .and().withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.EXITING).event(WeighEventEnum.LEAVING_WEIGH).action(weighAction.truckLeavingWeigh())
                .and().withExternal().source(WeighStatusEnum.EXITING).target(WeighStatusEnum.EXITED).event(WeighEventEnum.LEFT_WEIGH).action(weighAction.truckLeftWeigh())
                .and().withExternal().source(WeighStatusEnum.EXITED).target(WeighStatusEnum.TRUCK_LEAVING).event(WeighEventEnum.LEAVING).action(weighAction.truckLeaving())
                .and().withExternal().source(WeighStatusEnum.TRUCK_LEAVING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.LEFT).action(weighAction.truckLeft())
                .and().withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.TRUCK_LEAVING).event(WeighEventEnum.LEAVING).action(weighAction.truckLeaving())

                .and().withExternal().source(WeighStatusEnum.WAIT).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.TRUCK_FOUND).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.CARD_READ).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.ENTERING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.WEIGHING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.EXITING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.EXITED).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.TRUCK_LEAVING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                // 未进入到计量流程的状态下收到前端停止自动计量的请求时进入STOP_WAIT状态
                .and().withExternal().source(WeighStatusEnum.WAIT).target(WeighStatusEnum.STOP_WAIT).event(WeighEventEnum.STOP_WAIT).action(weighAction.stopWait())
                .and().withExternal().source(WeighStatusEnum.STOP_WAIT).target(WeighStatusEnum.WAIT).event(WeighEventEnum.START_WAIT).action(weighAction.startWait());

    }
}
