package com.regulyator.santabot.statemachine.action;

import com.regulyator.santabot.statemachine.GiftEvent;
import com.regulyator.santabot.statemachine.GiftState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartCreatingGiftAction implements Action<GiftState, GiftEvent> {

    @Override
    public void execute(StateContext<GiftState, GiftEvent> context) {
        context.getExtendedState().getVariables().clear();
    }
}
