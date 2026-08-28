package com.valuego.tourplace.api.dto.response;

import com.valuego.travel.entity.Travel;

import java.util.List;

public record AiScheduleResDto(List<Day> days) {
    public record Day(
            Integer dayNumber,
            List<Place> places
    ) {}

    public record Place(
            String contentId,
            String visitTime,
            Integer scheduleOrder,
            String placeType,
            String reason
    ) {}

    public static AiScheduleResDto from(Travel travel) {
        List<Day> days = travel.getDays().stream()
                .map(day -> new Day(
                        day.getDayNumber(),
                        day.getPlaces().stream()
                                .map(place -> new Place(
                                        place.getContentId(),
                                        place.getVisitTime() != null ? place.getVisitTime().toString() : null,
                                        place.getScheduleOrder(),
                                        place.getPlaceType(),
                                        place.getReason()
                                ))
                                .toList()
                ))
                .toList();

        return new AiScheduleResDto(days);
    }
}
