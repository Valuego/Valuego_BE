package com.valuego.tourplace.api.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AiScheduleResDto {

    private List<Day> days;

    @Getter
    @NoArgsConstructor
    public static class Day {

        private Integer dayNumber;

        private List<Place> places;
    }

    @Getter
    @NoArgsConstructor
    public static class Place {

        private String contentId;

        private Integer scheduleOrder;

        private String placeType;

        private String reason;
    }
}
