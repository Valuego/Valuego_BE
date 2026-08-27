package com.valuego.travel.api.dto.response;

import com.valuego.travel.entity.TravelPlace;

public record TravelPlaceInfoResDto(
        Long travelPlaceId,
        String contentId,
        Integer scheduleOrder,
        String placeType,
        String reason,
        String name,
        String address,
        String imageUrl,
        Double latitude,
        Double longitude,
        Double distanceFromPreviousKm
) {
    public static TravelPlaceInfoResDto from(TravelPlace place) {
        return new TravelPlaceInfoResDto(
                place.getId(),
                place.getContentId(),
                place.getScheduleOrder(),
                place.getPlaceType(),
                place.getReason(),
                place.getPlaceName(),
                place.getAddress(),
                place.getImageUrl(),
                place.getLatitude(),
                place.getLongitude(),
                place.getDistanceFromPreviousKm()
        );
    }
}
