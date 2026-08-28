package com.valuego.travel.entity;

import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // 한국관광공사 contentId
    @Column(nullable = false)
    private String contentId;

    private String contentTypeId;
    private LocalTime visitTime;

    @Column(nullable = false)
    private Integer scheduleOrder;

    // TOURIST_SPOT / RESTAURANT
    @Column(nullable = false)
    private String placeType;

    @Column(length = 500)
    private String reason;

    private Double distanceFromPreviousKm;

    @Builder
    public TravelPlace(TravelDay travelDay, Group group, String contentId, String contentTypeId, LocalTime visitTime,
                       Integer scheduleOrder, String placeType, String reason,
                       Double distanceFromPreviousKm) {
        this.travelDay = travelDay;
        this.group = group;
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
        this.visitTime = visitTime;
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
