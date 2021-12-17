package com.regulyator.santabot.service.draw;

import com.regulyator.santabot.domain.dto.GiftDto;

import java.util.List;

public interface GiftDrawingService {

    void drawGifts(List<GiftDto> gifts);
}
