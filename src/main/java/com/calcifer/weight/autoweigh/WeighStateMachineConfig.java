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
                .states(EnumSet.allOf(WeighStatusEnum.class));
    }

    /**
     * 配置状态转换事件关系
     */
    public void configure(StateMachineTransitionConfigurer<WeighStatusEnum, WeighEventEnum> transitions) throws Exception {
        transitions
                .withExternal().source(WeighStatusEnum.WAIT).target(WeighStatusEnum.WAIT_CARD).event(WeighEventEnum.TRUCK_FOUND).action(weighAction.foundTruck())
                .and()
                .withExternal().source(WeighStatusEnum.WAIT_CARD).target(WeighStatusEnum.WAIT_ENTER).event(WeighEventEnum.READ_CARD).action(weighAction.waitTruckEntering())
                .and()
                .withExternal().source(WeighStatusEnum.WAIT_CARD).target(WeighStatusEnum.WAIT).event(WeighEventEnum.CANCEL_ENTER).action(weighAction.truckLeave())
                .and()
                .withExternal().source(WeighStatusEnum.WAIT_ENTER).target(WeighStatusEnum.ENTERING).event(WeighEventEnum.ENTER).action(weighAction.truckEntering())
                .and()
                .withExternal().source(WeighStatusEnum.ENTERING).target(WeighStatusEnum.ON_WEIGH).event(WeighEventEnum.ENTERED).action(weighAction.truckEntered())
                .and()
                .withExternal().source(WeighStatusEnum.ON_WEIGH).target(WeighStatusEnum.WEIGHED).event(WeighEventEnum.WEIGHED).action(weighAction.weigh())
                .and()
                .withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.LEAVING_WEIGH).event(WeighEventEnum.LEAVING_WEIGH).action(weighAction.truckLeavingWeigh())
                .and()
                .withExternal().source(WeighStatusEnum.LEAVING_WEIGH).target(WeighStatusEnum.LEFT_WEIGH).event(WeighEventEnum.LEFT_WEIGH).action(weighAction.truckLeftWeigh())
                .and()
                .withExternal().source(WeighStatusEnum.LEFT_WEIGH).target(WeighStatusEnum.LEAVING).event(WeighEventEnum.LEAVING).action(weighAction.truckLeaving())
                .and()
                .withExternal().source(WeighStatusEnum.LEAVING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.LEFT).action(weighAction.truckLeft())
                .and()
                .withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.LEAVING).event(WeighEventEnum.LEAVING).action(weighAction.truckLeaving())
                .and()

                .withExternal().source(WeighStatusEnum.WAIT).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.WAIT_CARD).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.WAIT_ENTER).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.ENTERING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.ON_WEIGH).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.WEIGHED).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.LEAVING_WEIGH).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.LEFT_WEIGH).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset())
                .and().withExternal().source(WeighStatusEnum.LEAVING).target(WeighStatusEnum.WAIT).event(WeighEventEnum.RESET).action(weighAction.reset());
    }
}
