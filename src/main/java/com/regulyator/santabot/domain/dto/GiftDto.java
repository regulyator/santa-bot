package com.regulyator.santabot.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GiftDto {
    private String id;
    private String userId;
    private String userChatId;
    private String santaUserId;
    private String santaChatId;
    private String addressToDeliver;
    private String giftDescription;
    @Builder.Default
    private Boolean delivered = false;
    @Builder.Default
    private Boolean draw = false;

}
