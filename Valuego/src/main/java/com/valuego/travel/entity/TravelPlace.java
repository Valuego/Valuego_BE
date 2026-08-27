package com.valuego.travel.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_place_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_day_id", nullable = false)
    private TravelDay travelDay;

    // 한국관광공사 contentId
    @Column(nullable = false)
    private String contentId;

    @Column(nullable = false)
    private String placeName;

    private String address;

    private String imageUrl;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer scheduleOrder;

    // TOURIST_SPOT / RESTAURANT
    @Column(nullable = false)
    private String placeType;

    @Column(length = 500)
    private String reason;

    private Double distanceFromPreviousKm;

    @Builder
    public TravelPlace(TravelDay travelDay, String contentId, String placeName, String address,
                       String imageUrl, Double latitude, Double longitude,
                       Integer scheduleOrder, String placeType, String reason,
                       Double distanceFromPreviousKm) {
        this.travelDay = travelDay;
        this.contentId = contentId;
        this.placeName = placeName;
        this.address = address;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleOrder = scheduleOrder;
        this.placeType = placeType;
        this.reason = reason;
        this.distanceFromPreviousKm = distanceFromPreviousKm;
    }

    public void assignTravelDay(TravelDay travelDay) {
        if (this.travelDay != null) {
            this.travelDay.getPlaces().remove(this);
        }
        this.travelDay = travelDay;
        if (travelDay != null && !travelDay.getPlaces().contains(this)) {
            travelDay.getPlaces().add(this);
        }
    }

    public void updateDistanceFromPrevious(Double distance) {
        this.distanceFromPreviousKm = distance;
    }
}
