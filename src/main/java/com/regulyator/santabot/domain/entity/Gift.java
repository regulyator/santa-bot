package com.regulyator.santabot.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gift {
    @Id
    private String id;
    @Field("userId")
    private String userId;
    @Field("userChatId")
    private String userChatId;
    @Field("santaUserId")
    private String santaUserId;
    @Field("santaChatId")
    private String santaChatId;
    @Field("addressToDeliver")
    private String addressToDeliver;
    @Field("giftDescription")
    private String giftDescription;
    @Builder.Default
    @Field("delivered")
    private boolean delivered = false;
    @Builder.Default
    @Field("draw")
    private Boolean draw = false;

}
