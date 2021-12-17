package com.regulyator.santabot.statemachine.action;

import com.regulyator.santabot.domain.dto.GiftDto;
import com.regulyator.santabot.service.GiftService;
import com.regulyator.santabot.statemachine.GiftEvent;
import com.regulyator.santabot.statemachine.GiftState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class FinishGiftFillAction implements Action<GiftState, GiftEvent> {
    private static final String GIFT_VAR_KEY = "GIFT";
    private final GiftService giftService;

    @Autowired
    public FinishGiftFillAction(GiftService giftService) {
        this.giftService = giftService;
    }

    @Override
    public void execute(StateContext<GiftState, GiftEvent> context) {
        final var gift = context.getExtendedState().get(GIFT_VAR_KEY, GiftDto.class);
        if (Objects.nonNull(gift)) {
            giftService.saveGift(gift);
            log.info("gift created");
        }
    }
}
