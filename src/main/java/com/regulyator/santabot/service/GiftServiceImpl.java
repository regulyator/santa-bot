package com.regulyator.santabot.service;

import com.regulyator.santabot.domain.dto.GiftDto;
import com.regulyator.santabot.domain.entity.Gift;
import com.regulyator.santabot.exception.GiftServiceException;
import com.regulyator.santabot.repository.GiftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GiftServiceImpl implements GiftService {
    private final GiftRepository giftRepository;

    public GiftServiceImpl(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    @Override
    public void saveGift(GiftDto giftDto) {
        try {
            giftRepository.save(Gift.builder()
                    .id(giftDto.getId())
                    .userId(giftDto.getUserId())
                    .userChatId(giftDto.getUserChatId())
                    .santaUserId(giftDto.getSantaUserId())
                    .santaChatId(giftDto.getSantaChatId())
                    .giftDescription(giftDto.getGiftDescription())
                    .addressToDeliver(giftDto.getAddressToDeliver())
                    .delivered(giftDto.getDelivered())
                    .draw(giftDto.getDraw())
                    .build());
        } catch (Exception ex) {
            log.error("Error when saving gift {}", ex.getMessage());
            throw new GiftServiceException("Error when saving gift");
        }
    }

    @Override
    public void saveGifts(List<GiftDto> gifts) {
        try {
            giftRepository.saveAll(gifts.stream().map(giftDto -> Gift.builder()
                    .id(giftDto.getId())
                    .userId(giftDto.getUserId())
                    .userChatId(giftDto.getUserChatId())
                    .santaUserId(giftDto.getSantaUserId())
                    .santaChatId(giftDto.getSantaChatId())
                    .giftDescription(giftDto.getGiftDescription())
                    .addressToDeliver(giftDto.getAddressToDeliver())
                    .delivered(giftDto.getDelivered())
                    .draw(giftDto.getDraw())
                    .build()).collect(Collectors.toList()));
        } catch (Exception ex) {
            log.error("Error when saving gifts {}", ex.getMessage());
            throw new GiftServiceException("Error when saving gifts");
        }
    }

    @Override
    public GiftDto getUserGift(long userId) {
        try {
            return giftRepository.getByUserId(String.valueOf(userId))
                    .map(g -> GiftDto.builder()
                            .id(g.getId())
                            .userId(g.getUserId())
                            .userChatId(g.getUserChatId())
                            .santaUserId(g.getSantaUserId())
                            .santaChatId(g.getSantaChatId())
                            .giftDescription(g.getGiftDescription())
                            .addressToDeliver(g.getAddressToDeliver())
                            .draw(g.getDraw())
                            .build())
                    .orElse(null);
        } catch (Exception ex) {
            log.error("Error when getting gift by userId = {} {}", userId, ex.getMessage());
            throw new GiftServiceException("Error when getting gift by userId");
        }

    }

    @Override
    public GiftDto removeGift(long userId) {
        try {
            return giftRepository.deleteByUserId(String.valueOf(userId))
                    .map(g -> GiftDto.builder()
                            .id(g.getId())
                            .userId(g.getUserId())
                            .userChatId(g.getUserChatId())
                            .santaUserId(g.getSantaUserId())
                            .santaChatId(g.getSantaChatId())
                            .giftDescription(g.getGiftDescription())
                            .addressToDeliver(g.getAddressToDeliver())
                            .draw(g.getDraw())
                            .build())
                    .orElse(null);
        } catch (Exception ex) {
            log.error("Error when remove gift by userId = {} {}", userId, ex.getMessage());
            throw new GiftServiceException("Error when remove gift by userId");
        }
    }

    @Override
    public boolean isUserGiftExist(long userId) {
        return giftRepository.existsByUserId(String.valueOf(userId));
    }

    @Override
    public List<GiftDto> getUnDrowningGifts() {
        return giftRepository.getAllNotDraw().stream()
                .map(g -> GiftDto.builder()
                        .id(g.getId())
                        .userId(g.getUserId())
                        .userChatId(g.getUserChatId())
                        .santaUserId(g.getSantaUserId())
                        .santaChatId(g.getSantaChatId())
                        .giftDescription(g.getGiftDescription())
                        .addressToDeliver(g.getAddressToDeliver())
                        .draw(g.getDraw())
                        .build())
                .collect(Collectors.toList());
    }
}
