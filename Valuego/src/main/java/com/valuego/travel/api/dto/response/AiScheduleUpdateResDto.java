package com.valuego.travel.api.dto.response;

import java.util.List;

public record AiScheduleUpdateResDto(
        String summaryTitle,
        Integer dayNumber,
        OriginalPlaceDto originalPlace,
        List<NewPlaceDto> newPlaces
) {
    // 취소선으로 표시될 기존 장소 정보
    public record OriginalPlaceDto(
            String contentId,
            String visitTime,
            String placeName
    ) {}

    // 변경 및 새로 추가되는 장소 정보
    public record NewPlaceDto(
            String contentId,
            String visitTime,
            String placeType,
            String reason
    ) {}
}
