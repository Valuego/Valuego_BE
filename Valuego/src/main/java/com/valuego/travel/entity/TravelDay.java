package com.valuego.travel.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_day_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private Double totalDistanceKm = 0.0;

    @OneToMany(mappedBy = "travelDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("scheduleOrder ASC")
    private List<TravelPlace> places = new ArrayList<>();

    public TravelDay(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void assignTravel(Travel travel) {
        if (this.travel != null) {
            this.travel.getDays().remove(this);
        }
        this.travel = travel;
        if (travel != null && !travel.getDays().contains(this)) {
            travel.getDays().add(this);
        }
    }

    public void addPlace(TravelPlace place) {
        places.add(place);
        place.assignTravelDay(this);
    }

    public void updateTotalDistance(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }
}
