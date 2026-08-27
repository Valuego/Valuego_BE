package com.valuego.tourplace.api.dto.response;

import com.valuego.travel.entity.Travel;
import lombok.Builder;
import lombok.Getter;

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
        private String contentId;
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
                                                                .contentId(place.getContentId())
                                                                .name(place.getPlaceName())
                                                                .address(place.getAddress())
                                                                .imageUrl(place.getImageUrl())
                                                                .latitude(place.getLatitude())
                                                                .longitude(place.getLongitude())
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
}
