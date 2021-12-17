package com.regulyator.santabot.service.draw;

import com.regulyator.santabot.domain.dto.GiftDto;
import com.regulyator.santabot.service.GiftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GiftDrawingDirectorImpl implements GiftDrawingDirector {
    private static final Predicate<GiftDto> FILTER_SAVE_PREDICATE = giftDto -> Objects.nonNull(giftDto.getSantaUserId());
    private static final int DRAW_TIMER = 30000;
    private final GiftService giftService;
    private final GiftDrawingService giftDrawingService;
    private final TelegramLongPollingBot santaBot;

    @Autowired
    public GiftDrawingDirectorImpl(GiftService giftService,
                                   GiftDrawingService giftDrawingService,
                                   TelegramLongPollingBot santaBot) {
        this.giftService = giftService;
        this.giftDrawingService = giftDrawingService;
        this.santaBot = santaBot;
    }

    @Override
    @Scheduled(fixedDelay = DRAW_TIMER)
    public void drawGifts() {
        var sourceGifts = giftService.getUnDrowningGifts();
        giftDrawingService.drawGifts(sourceGifts);
        var updatedGifts = sourceGifts.stream()
                .filter(FILTER_SAVE_PREDICATE)
                .collect(Collectors.toList());
        giftService.saveGifts(updatedGifts);
        sendUserNotifications(updatedGifts);

    }

    private void sendUserNotifications(List<GiftDto> updatedGifts) {
        updatedGifts.forEach(this::sendUserUpdateMessage);
    }

    private void sendUserUpdateMessage(GiftDto giftDto){
        var message = SendMessage.builder()
                .chatId(giftDto.getUserChatId())
                .text("Ваш подарок уже в пути:)")
                .build();
        var messageHelp = SendMessage.builder()
                .chatId(giftDto.getSantaChatId())
                .text(getGiftHelpText(giftDto))
                .build();
        try {
            santaBot.execute(message);
            santaBot.execute(messageHelp);
        } catch (TelegramApiException e) {
            log.error("Error send user update {}", e.getMessage());
        }
    }

    private String getGiftHelpText(GiftDto giftDto) {
        return String.format("Санте нужна Ваша помощь с подарками!%nОн хочет поручить Вам доставить подарок:)%n" +
                "Описание подарка: %s%nАдрес: %s", giftDto.getGiftDescription(), giftDto.getAddressToDeliver());
    }
}
