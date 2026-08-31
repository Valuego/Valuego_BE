package com.valuego.travel.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.travel.entity.TravelPlace;

import java.time.LocalTime;

public record TravelPlaceInfoResDto(
        Long travelPlaceId,
        String contentId,
        @JsonFormat(pattern = "HH:mm")
        LocalTime visitTime,
        Integer scheduleOrder,
        String placeType,
        String reason,
        String name,
        String address,
        String imageUrl,
        String memoUrl,
        Double latitude,
        Double longitude,
        Double distanceFromPreviousKm
) {
    public static TravelPlaceInfoResDto of(TravelPlace place, TourPlace liveData) {
        String displayName = (place.getCustomName() != null && !place.getCustomName().isBlank())
                ? place.getCustomName()
                : (liveData != null ? liveData.getName() : "장소 정보 없음");

        return new TravelPlaceInfoResDto(
                place.getId(),
                place.getContentId(),
                place.getVisitTime(),
                place.getScheduleOrder(),
                place.getPlaceType(),
                place.getReason(),
                displayName,
                liveData != null ? liveData.getAddress() : "",
                liveData != null ? liveData.getImageUrl() : "",
                place.getMemoUrl(),
                liveData != null ? liveData.getLatitude() : null,
                liveData != null ? liveData.getLongitude() : null,
                place.getDistanceFromPreviousKm()
        );
    }

    public static TravelPlaceInfoResDto of(TravelPlace place) {
        return new TravelPlaceInfoResDto(
                place.getId(),
                place.getContentId(),
                place.getVisitTime(),
                place.getScheduleOrder(),
                place.getPlaceType(),
                place.getReason(),
                place.getCustomName(),
                "",
                "",
                place.getMemoUrl(),
                null,
                null,
                place.getDistanceFromPreviousKm()
        );
    }
}
