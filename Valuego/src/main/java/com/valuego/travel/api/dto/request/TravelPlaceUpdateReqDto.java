package com.valuego.travel.api.dto.request;

import java.time.LocalTime;

public record TravelPlaceUpdateReqDto(
        String customName,
        Integer scheduleOrder,
        LocalTime visitTime,
        String memoUrl
) {
}
