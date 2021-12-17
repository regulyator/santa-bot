package com.regulyator.santabot.service;

public interface GiftBuilderService {

    void initNewGift(long userId, long chatId);

    void resetGiftCreation(long userId);

    void processGift(long userId, long chatId, String inputText);

}
