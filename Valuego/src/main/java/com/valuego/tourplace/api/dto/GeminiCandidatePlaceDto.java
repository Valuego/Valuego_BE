package com.valuego.tourplace.api.dto;

import com.valuego.tourplace.api.dto.response.TourPlace;
import lombok.Getter;

@Getter
public class GeminiCandidatePlaceDto {
    private final String id;      // contentId
    private final String name;    // 관광지/식당명
    private final String type;       // contentTypeId (12, 14, 28, 38, 39 등)
    private final Double latitude;
    private final Double longitude;

    public GeminiCandidatePlaceDto(TourPlace place, Double latitude, Double longitude) {
        this.id = place.getContentId();
        this.name = place.getName();
        this.type = place.getContentTypeId();
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
