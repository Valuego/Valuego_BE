package com.valuego.travel.api.dto.response;

import java.util.List;

public record AiScheduleUpdateResDto(
        String summaryTitle,      // 예: "AI 제안 - Day1 오후만 다시 짰어요"
        Integer dayNumber,
        OriginalPlaceDto originalPlace, // 기존에 변경/취소되는 장소 (사진의 취소선 부분)
        List<NewPlaceDto> newPlaces     // 화살표(->) 뒤로 새로 추천되는 장소 목록
) {
    // 취소선으로 표시될 기존 장소 정보
    public record OriginalPlaceDto(
            String contentId,
            String visitTime,     // 예: "15:00"
            String placeName      // 예: "감천문화마을 (2시간)"
    ) {}

    // 변경 및 새로 추가되는 장소 정보
    public record NewPlaceDto(
            String contentId,
            String visitTime,     // 예: "15:30"
            String placeType,     // TOUR / RESTAURANT
            String reason         // 예: "송도 해상케이블카 (1시간 · 앉아서 뷰)"
    ) {}
}
