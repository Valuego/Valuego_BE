package com.valuego.travel.entity.repository;

import com.valuego.travel.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    Optional<Travel> findByGroupId(Long groupId);
}
