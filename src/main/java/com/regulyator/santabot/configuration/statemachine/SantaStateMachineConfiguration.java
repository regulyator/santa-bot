package com.regulyator.santabot.configuration.statemachine;

import com.regulyator.santabot.statemachine.GiftEvent;
import com.regulyator.santabot.statemachine.GiftState;
import com.regulyator.santabot.statemachine.SantaStateMachinePersistStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Objects;

@Configuration
@EnableStateMachineFactory(name = "santaStateMachine")
@Slf4j
public class SantaStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<GiftState, GiftEvent> {
    private final Action<GiftState, GiftEvent> finishGiftFillAction;
    private final Action<GiftState, GiftEvent> startCreatingGiftAction;

    @Autowired
    public SantaStateMachineConfiguration(@Lazy Action<GiftState, GiftEvent> finishGiftFillAction,
                                          @Lazy Action<GiftState, GiftEvent> startCreatingGiftAction) {
        this.finishGiftFillAction = finishGiftFillAction;
        this.startCreatingGiftAction = startCreatingGiftAction;
    }

    @Override
    public void configure(final StateMachineConfigurationConfigurer<GiftState, GiftEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<GiftState, GiftEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(GiftState.START).target(GiftState.ADD_DESCRIPTION)
                .event(GiftEvent.CREATE)
                .and()
                .withExternal()
                .source(GiftState.ADD_DESCRIPTION).target(GiftState.ADD_ADDRESS)
                .event(GiftEvent.CREATE)
                .and()
                .withExternal()
                .source(GiftState.ADD_ADDRESS).target(GiftState.CHECK)
                .event(GiftEvent.SUBMIT)
                .and()
                .withExternal()
                .source(GiftState.CHECK).target(GiftState.COMPLETED)
                .event(GiftEvent.FINISHED)
                .and()
                .withExternal()
                .source(GiftState.ADD_DESCRIPTION).target(GiftState.START)
                .action(startCreatingGiftAction)
                .event(GiftEvent.RESET)
                .and()
                .withExternal()
                .source(GiftState.ADD_ADDRESS).target(GiftState.START)
                .action(startCreatingGiftAction)
                .event(GiftEvent.RESET)
                .and()
                .withExternal()
                .source(GiftState.CHECK).target(GiftState.START)
                .action(startCreatingGiftAction)
                .event(GiftEvent.RESET)
                .and()
                .withExternal()
                .source(GiftState.COMPLETED).target(GiftState.START)
                .action(startCreatingGiftAction)
                .event(GiftEvent.RESET);
    }

    @Override
    public void configure(final StateMachineStateConfigurer<GiftState, GiftEvent> states) throws Exception {
        states
                .withStates()
                .initial(GiftState.START)
                .state(GiftState.COMPLETED, finishGiftFillAction)
                .states(EnumSet.allOf(GiftState.class));
    }

    @Bean
    public StateMachinePersister<GiftState, GiftEvent, Long> persister() {
        return new DefaultStateMachinePersister<>(new SantaStateMachinePersistStorage());
    }


    @Bean
    public StateMachineListener<GiftState, GiftEvent> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<GiftState, GiftEvent> from, State<GiftState, GiftEvent> to) {
                if (Objects.nonNull(from)) {
                    log.info("State changed to {}", to.getId());
                }
            }
        };
    }
}
