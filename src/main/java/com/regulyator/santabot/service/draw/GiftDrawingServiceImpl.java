package com.regulyator.santabot.service.draw;

import com.regulyator.santabot.domain.dto.GiftDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class GiftDrawingServiceImpl implements GiftDrawingService {

    @Override
    public void drawGifts(List<GiftDto> gifts) {
        if (!CollectionUtils.isEmpty(gifts)) {
            GiftDto[] unDrowningGiftsArr = gifts.toArray(GiftDto[]::new);
            var length = unDrowningGiftsArr.length;
            if (length / 2 > 0) {
                var startIdx = 0;
                var endIdx = length - 1;
                while (startIdx < endIdx) {
                    unDrowningGiftsArr[endIdx].setSantaUserId(unDrowningGiftsArr[startIdx].getUserId());
                    unDrowningGiftsArr[startIdx].setSantaUserId(unDrowningGiftsArr[endIdx].getUserId());

                    unDrowningGiftsArr[endIdx].setSantaChatId(unDrowningGiftsArr[startIdx].getUserChatId());
                    unDrowningGiftsArr[startIdx].setSantaChatId(unDrowningGiftsArr[endIdx].getUserChatId());

                    unDrowningGiftsArr[endIdx].setDraw(true);
                    unDrowningGiftsArr[startIdx].setDraw(true);
                    startIdx++;
                    endIdx--;
                }
            }
        }
    }
}
