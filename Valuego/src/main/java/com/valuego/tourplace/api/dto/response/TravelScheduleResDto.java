package com.valuego.tourplace.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.valuego.travel.entity.Travel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class TravelScheduleResDto {

    private Long travelId;
    private List<Day> days;

    @Getter
    @Builder
    public static class Day {
        private Integer dayNumber;
        private Double totalDistanceKm;
        private List<Place> places;
    }

    @Getter
    @Builder
    public static class Place {
        private Long travelPlaceId;
        private String contentId;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime visitTime;

        private String name;
        private String address;
        private String imageUrl;
        private Double latitude;
        private Double longitude;
        private Integer scheduleOrder;
        private String placeType;
        private String reason;
        private Double distanceFromPreviousKm;
    }

    public static TravelScheduleResDto from(Travel travel) {
        return TravelScheduleResDto.builder()
                .travelId(travel.getId())
                .days(
                        travel.getDays().stream()
                                .map(day -> Day.builder()
                                        .dayNumber(day.getDayNumber())
                                        .totalDistanceKm(day.getTotalDistanceKm())
                                        .places(
                                                day.getPlaces().stream()
                                                        .map(place -> Place.builder()
                                                                .travelPlaceId(place.getId())
                                                                .contentId(place.getContentId())
                                                                .visitTime(place.getVisitTime())
                                                                .scheduleOrder(place.getScheduleOrder())
                                                                .placeType(place.getPlaceType())
                                                                .reason(place.getReason())
                                                                .distanceFromPreviousKm(
                                                                        place.getDistanceFromPreviousKm()
                                                                )
                                                                .build()
                                                        )
                                                        .toList()
                                        )
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

    // 2. 실시간 TourAPI 조회가 결합될 때 사용하는 변환 메서드
    public static TravelScheduleResDto of(Travel travel, java.util.Map<String, TourPlace> livePlaceMap) {
        return TravelScheduleResDto.builder()
                .travelId(travel.getId())
                .days(
                        travel.getDays().stream()
                                .map(day -> Day.builder()
                                        .dayNumber(day.getDayNumber())
                                        .totalDistanceKm(day.getTotalDistanceKm())
                                        .places(
                                                day.getPlaces().stream()
                                                        .map(place -> {
                                                            TourPlace liveData = livePlaceMap.get(place.getContentId());
                                                            return Place.builder()
                                                                    .travelPlaceId(place.getId())
                                                                    .contentId(place.getContentId())
                                                                    .visitTime(place.getVisitTime())
                                                                    .name(liveData != null ? liveData.getName() : null)
                                                                    .address(liveData != null ? liveData.getAddress() : null)
                                                                    .imageUrl(liveData != null ? liveData.getImageUrl() : null)
                                                                    .latitude(liveData != null ? liveData.getLatitude() : null)
                                                                    .longitude(liveData != null ? liveData.getLongitude() : null)
                                                                    .scheduleOrder(place.getScheduleOrder())
                                                                    .placeType(place.getPlaceType())
                                                                    .reason(place.getReason())
                                                                    .distanceFromPreviousKm(
                                                                            place.getDistanceFromPreviousKm()
                                                                    )
                                                                    .build();
                                                        })
                                                        .toList()
                                        )
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}
