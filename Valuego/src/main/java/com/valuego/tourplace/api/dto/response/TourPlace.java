package com.valuego.tourplace.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPlace {
    private String contentId;
    private String contentTypeId;
    private String name;
    private String address;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}
