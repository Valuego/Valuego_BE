package com.valuego.travel.entity.repository;

import com.valuego.travel.entity.TravelDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelDayRepository extends JpaRepository<TravelDay, Long> {
}
