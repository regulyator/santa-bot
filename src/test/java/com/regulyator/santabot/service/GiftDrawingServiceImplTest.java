package com.regulyator.santabot.service;

import com.regulyator.santabot.domain.dto.GiftDto;
import com.regulyator.santabot.service.draw.GiftDrawingService;
import com.regulyator.santabot.service.draw.GiftDrawingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GiftDrawingServiceImplTest {
    private final GiftDrawingService giftDrawingService = new GiftDrawingServiceImpl();

    @Test
    @DisplayName("should draw gifts")
    void shouldDrawGifts() {
        List<GiftDto> unDrawGifts = getUnDrawList();
        assertThat(unDrawGifts).allMatch(giftDto -> Objects.isNull(giftDto.getSantaUserId()));
        giftDrawingService.drawGifts(unDrawGifts);
        assertThat(unDrawGifts).allMatch(giftDto -> Objects.nonNull(giftDto.getSantaUserId()));
    }

    @Test
    @DisplayName("should not throw NPE")
    void shouldNotThrowNPEWhenNull() {
        assertDoesNotThrow(() -> giftDrawingService.drawGifts(null));
    }

    @Test
    @DisplayName("should not throw NPE")
    void shouldNotThrowNPEWhenEmptyList() {
        assertDoesNotThrow(() -> giftDrawingService.drawGifts(Collections.emptyList()));
    }

    private List<GiftDto> getUnDrawList() {
        return List.of(GiftDto.builder()
                        .userId("1")
                        .build(),
                GiftDto.builder()
                        .userId("2")
                        .build(),
                GiftDto.builder()
                        .userId("3")
                        .build(),
                GiftDto.builder()
                        .userId("4")
                        .build());
    }

}