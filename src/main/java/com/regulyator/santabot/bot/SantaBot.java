package com.regulyator.santabot.bot;

import com.regulyator.santabot.service.GiftBuilderService;
import com.regulyator.santabot.service.GiftService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class SantaBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private static final String GIFT_COMMAND = "/gift";
    private static final String RESET_COMMAND = "/reset";
    private static final String HELP_COMMAND = "/help";
    private final GiftBuilderService giftBuilderService;
    private final GiftService giftService;

    public SantaBot(@Lazy GiftBuilderService giftBuilderService,
                    GiftService giftService,
                    @Value("${santa.bot.token:token}") String token,
                    @Value("${santa.bot.name:name}") String name) {
        super();
        this.giftBuilderService = giftBuilderService;
        this.giftService = giftService;
        this.token = token;
        this.name = name;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            final var user = update.getMessage().getFrom();
            final var chatId = update.getMessage().getChatId();
            final var messageText = update.getMessage().getText();
            switch (messageText) {
                case GIFT_COMMAND:
                    processGiftCommand(user, chatId);
                    break;
                case RESET_COMMAND:
                    processResetCommand(user, chatId);
                    break;
                case HELP_COMMAND:
                    processHelpCommand(user, chatId);
                    break;
                default:
                    processOther(user, messageText, chatId);
                    break;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void processResetCommand(User user, Long chatId) {
        giftService.removeGift(user.getId());
        giftBuilderService.resetGiftCreation(user.getId());
        sendMessage(String.valueOf(chatId), "Готово, можно отправить новое письмо!");
        sendMessage(String.valueOf(chatId), String.format("Чтобы написать письмо санте, введите команду /gift%nЧтобы начать заново, введите команду /reset"));
    }

    private void processHelpCommand(User user, Long chatId) {
        sendMessage(String.valueOf(chatId), String.format("Чтобы написать письмо санте, введите команду /gift%nЧтобы начать заново, введите команду /reset"));
    }

    private void processOther(User user, String messageText, Long chatId) {
        giftBuilderService.processGift(user.getId(), chatId, messageText);
    }

    private void processGiftCommand(User user, Long chatId) {
        if (!giftService.isUserGiftExist(user.getId())) {
            giftBuilderService.initNewGift(user.getId(), chatId);
        } else {
            sendMessage(String.valueOf(chatId), String.format("Вы уже отправили письмо Санте;)%nЧтобы написать новое письмо, введите команду /reset"));
        }
    }

    private void sendMessage(String chatId, String messageText) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
