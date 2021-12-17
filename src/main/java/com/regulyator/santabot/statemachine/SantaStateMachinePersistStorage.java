package com.regulyator.santabot.statemachine;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;
import java.util.Map;

public class SantaStateMachinePersistStorage implements StateMachinePersist<GiftState, GiftEvent, Long> {
    private final Map<Long, StateMachineContext<GiftState, GiftEvent>> sMachinesContexts = new HashMap<>();

    @Override
    public void write(StateMachineContext<GiftState, GiftEvent> stateMachine, Long userId) throws Exception {
        sMachinesContexts.put(userId, stateMachine);
    }

    @Override
    public StateMachineContext<GiftState, GiftEvent> read(Long userId) throws Exception {
        return sMachinesContexts.get(userId);
    }
}
