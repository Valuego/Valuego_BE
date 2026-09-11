package com.valuego.effort.entity.repository;

import com.valuego.effort.entity.Effort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EffortRepository extends JpaRepository<Effort, Long> {
    List<Effort> findByGroupIdAndTargetMemberId(Long groupId, Long targetMemberId);
}
