package com.valuego.tourplace.service;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.tourplace.api.dto.response.TourApiResDto;
import com.valuego.tourplace.api.dto.response.TourPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourApiService {

    private final RestClient restClient;

    @Value("${tour.api.service-key}")
    private String serviceKey;

    public List<TourPlace> getPlaces(Destination destination, int contentTypeId, int numOfRows) {

        TourApiResDto response =
                restClient.get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/areaBasedList2")
                                        .queryParam("serviceKey", serviceKey)
                                        .queryParam("numOfRows", numOfRows)
                                        .queryParam("pageNo", 1)
                                        .queryParam("MobileOS", "ETC")
                                        .queryParam("MobileApp", "ValueGo")
                                        .queryParam("_type", "json")
                                        .queryParam("lDongRegnCd", getLDongRegnCd(destination))
                                        .queryParam("lDongSignguCd", getLDongSignguCd(destination))
                                        .queryParam("contentTypeId", contentTypeId)
                                        .queryParam("arrange", "Q")
                                        .build()
                        )
                        .retrieve()
                        .body(TourApiResDto.class);

        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null
                || response.getResponse().getBody().getItems().getItem() == null) {

            return List.of();
        }

        return response.getResponse()
                .getBody()
                .getItems()
                .getItem()
                .stream()
                .map(this::convert)
                .filter(this::hasCoordinate)
                .toList();
    }

    private TourPlace convert(TourApiResDto.Item item) {
        return TourPlace.builder()
                .contentId(item.getContentId())
                .contentTypeId(item.getContentTypeId())
                .name(item.getTitle())
                .address(item.getAddr1())
                .imageUrl(item.getFirstimage())
                .latitude(parseDouble(item.getMapy()))
                .longitude(parseDouble(item.getMapx()))
                .build();
    }

    private boolean hasCoordinate(TourPlace place) {
        return place.getLatitude() != null && place.getLongitude() != null;
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getLDongRegnCd(Destination destination) {
        return switch (destination) {
            case BUSAN -> "26";
            case GANGNEUNG, SOKCHO -> "51";
            case GYEONGJU -> "47";
            case YEOSU -> "46";
            case JEONJU -> "45";
        };
    }

    private String getLDongSignguCd(Destination destination) {
        return switch (destination) {
            case BUSAN -> null;
            case GANGNEUNG -> "150";
            case SOKCHO -> "210";
            case GYEONGJU -> "130";
            case YEOSU -> "130";
            case JEONJU -> "110";
        };
    }
}
