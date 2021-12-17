package com.regulyator.santabot.service;

import com.regulyator.santabot.domain.dto.GiftDto;
import com.regulyator.santabot.statemachine.GiftEvent;
import com.regulyator.santabot.statemachine.GiftState;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@Slf4j
public class GiftBuilderServiceImpl implements GiftBuilderService {
    private static final String GIFT_VAR_KEY = "GIFT";
    private static final String YES = "да";
    private final StateMachineFactory<GiftState, GiftEvent> stateMachineFactory;
    private final StateMachinePersister<GiftState, GiftEvent, Long> persister;
    private final TelegramLongPollingBot santaBot;

    @Autowired
    public GiftBuilderServiceImpl(StateMachineFactory<GiftState, GiftEvent> stateMachineFactory,
                                  StateMachinePersister<GiftState, GiftEvent, Long> persister,
                                  TelegramLongPollingBot santaBot) {
        this.stateMachineFactory = stateMachineFactory;
        this.persister = persister;
        this.santaBot = santaBot;
    }

    @SneakyThrows
    @Override
    public void initNewGift(long userId, long chatId) {
        var stateMachine = getStateMachine(userId);
        if (!stateMachine.isComplete()
                && stateMachine.getState().getId().equals(GiftState.START)) {
            var newGift = GiftDto.builder()
                    .userId(String.valueOf(userId))
                    .userChatId(String.valueOf(chatId))
                    .build();
            stateMachine.getExtendedState().getVariables().put(GIFT_VAR_KEY, newGift);
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(GiftEvent.CREATE).build()))
                    .subscribe();
            persister.persist(stateMachine, userId);
            sendMessage(chatId, "Отлично! Пожалуйста ответьте на несколько вопросов.\n" +
                    "Чтобы начать заново, введите команду /reset");
            sendMessage(chatId, "В паре слов опишите, что бы Вы хотели получить от Санты?;)");
        }
    }

    @SneakyThrows
    @Override
    public void resetGiftCreation(long userId) {
        var stateMachine = getStateMachine(userId);
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(GiftEvent.RESET).build()))
                .subscribe();
        persister.persist(stateMachine, userId);

    }

    @SneakyThrows
    @Override
    public void processGift(long userId, long chatId, String inputText) {
        var stateMachine = getStateMachine(userId);
        switch (stateMachine.getState().getId()) {
            case ADD_DESCRIPTION:
                stateMachine.getExtendedState().get(GIFT_VAR_KEY, GiftDto.class)
                        .setGiftDescription(inputText);
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(GiftEvent.CREATE).build()))
                        .subscribe();
                sendMessage(chatId, "Куда Санта может доставить подарок?");
                break;
            case ADD_ADDRESS:
                stateMachine.getExtendedState().get(GIFT_VAR_KEY, GiftDto.class)
                        .setAddressToDeliver(inputText);
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(GiftEvent.SUBMIT).build()))
                        .subscribe();
                sendMessage(chatId, "Проверьте, все ли верно?");
                sendMessage(chatId, giftToString(stateMachine.getExtendedState().get(GIFT_VAR_KEY, GiftDto.class)));
                break;
            case CHECK:
                if (inputText.equalsIgnoreCase(YES)) {
                    stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(GiftEvent.FINISHED).build()))
                            .subscribe();
                    sendMessage(chatId, "Здорово, Санта уже в пути!\nМы сообщим дополнительно, когда подарок будет на месте:)");
                }
                break;
            default:
                sendMessage(chatId, "Если не знаете с чего начать, введите комманду /help");
                break;
        }
        persister.persist(stateMachine, userId);
    }

    private String giftToString(GiftDto gift) {
        if (Objects.nonNull(gift)) {
            return String.format("Описание подарка: %s%nАдрес: %s", gift.getGiftDescription(), gift.getAddressToDeliver());
        }
        return "";
    }

    @SneakyThrows
    private StateMachine<GiftState, GiftEvent> getStateMachine(long userId) {
        var stateMachine = stateMachineFactory.getStateMachine();
        persister.restore(stateMachine, userId);
        return stateMachine;
    }

    @SneakyThrows
    private void sendMessage(long chatId, String messageText) {
        var message = new SendMessage(String.valueOf(chatId), messageText);
        message.enableMarkdown(true);
        santaBot.execute(message);
    }
}
