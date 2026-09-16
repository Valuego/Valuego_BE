package com.valuego.users.api.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserTimelineResDto(
        int currentDay,
        int totalExpense,
        List<TimelineItemDto> items
) {
    @Builder
    public record TimelineItemDto(
            Long id,
            String title,
            LocalDateTime time,
            String category,    // 이동, 관광, 룰렛, 사다리
            String description  // 추가 설명
    ) {}
}
