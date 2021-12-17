package com.regulyator.santabot.service;

import com.regulyator.santabot.domain.dto.GiftDto;

import java.util.List;

public interface GiftService {

    void saveGift(GiftDto giftDto);

    void saveGifts(List<GiftDto> gifts);

    GiftDto getUserGift(long userId);

    GiftDto removeGift(long userId);

    boolean isUserGiftExist(long userId);

    List<GiftDto> getUnDrowningGifts();
}
