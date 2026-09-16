package com.valuego.users.api.dto.response;

import com.valuego.groups.entity.Group;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.travel.entity.TravelPlace;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Builder
public record UserScheduleResDto(
        Long groupId,
        String scheduleStatus, // 여행 중
        String groupTitle,
        int currentDay,
        String currentStatus,
        int totalExpense,
        List<RemainingScheduleResDto> todaySchedules
) {
    public static UserScheduleResDto of(
            Group group,
            int currentDay,
            String currentStatus,
            int totalExpense,
            List<RemainingScheduleResDto> todaySchedules
    ) {
        return UserScheduleResDto.builder()
                .groupId(group.getId())
                .scheduleStatus("여행 중")
                .groupTitle(group.getTitle())
                .currentDay(currentDay)
                .currentStatus(currentStatus)
                .totalExpense(totalExpense)
                .todaySchedules(todaySchedules)
                .build();
    }

    @Builder
    public record RemainingScheduleResDto(
            Long travelPlaceId,
            LocalDateTime time,
            String placeName,
            String category
    ) {
        public static RemainingScheduleResDto from(TravelPlace place, LocalDate today, Map<String, TourPlace> tourPlaceMap) {
            LocalTime time = place.getVisitTime() != null ? place.getVisitTime() : LocalTime.MIDNIGHT;
            LocalDateTime eventDateTime = LocalDateTime.of(today, time);

            TourPlace tourPlace = tourPlaceMap.get(place.getContentId());
            String placeName = (tourPlace != null) ? tourPlace.getName() : "장소 정보 없음";

            return RemainingScheduleResDto.builder()
                    .travelPlaceId(place.getId())
                    .time(eventDateTime)
                    .placeName(placeName)
                    .category(place.getPlaceType() != null ? place.getPlaceType() : "관광")
                    .build();
        }
    }
}
