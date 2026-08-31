package com.valuego.travel.entity.repository;

import com.valuego.travel.entity.TravelDay;
import com.valuego.travel.entity.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    void deleteAllByTravelDay(TravelDay travelDay);
}
