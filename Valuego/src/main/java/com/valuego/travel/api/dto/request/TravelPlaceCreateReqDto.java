package com.valuego.travel.api.dto.request;

import java.time.LocalTime;

public record TravelPlaceCreateReqDto(
        Long travelDayId,
        Integer scheduleOrder,
        String customName,
        LocalTime visitTime,
        String memoUrl
) {
}
