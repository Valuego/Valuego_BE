package com.valuego.travel.entity.repository;

import com.valuego.travel.entity.TravelDay;
import com.valuego.travel.entity.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    void deleteAllByTravelDay(TravelDay travelDay);

    @Query("SELECT tp FROM TravelPlace tp " +
            "JOIN tp.travelDay td " +
            "WHERE tp.group.id = :groupId AND td.dayNumber = :dayNumber " +
            "ORDER BY tp.visitTime ASC, tp.scheduleOrder ASC")
    List<TravelPlace> findAllByGroupIdAndDayNumber(@Param("groupId") Long groupId, @Param("dayNumber") Integer dayNumber);

    @Query("SELECT tp FROM TravelPlace tp " +
            "JOIN tp.travelDay td " +
            "WHERE tp.group.id = :groupId AND td.dayNumber = :dayNumber AND tp.visitTime >= :nowTime " +
            "ORDER BY tp.visitTime ASC, tp.scheduleOrder ASC")
    List<TravelPlace> findRemainingPlacesByGroupIdAndDayNumber(
            @Param("groupId") Long groupId,
            @Param("dayNumber") Integer dayNumber,
            @Param("nowTime") LocalTime nowTime
    );

    @Query("SELECT SUM(tp.distanceFromPreviousKm) FROM TravelPlace tp WHERE tp.group.id = :groupId")
    BigDecimal sumDistanceFromPreviousKmByGroupId(@Param("groupId") Long groupId);
}
