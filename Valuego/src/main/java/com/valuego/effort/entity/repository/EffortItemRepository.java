package com.valuego.effort.entity.repository;

import com.valuego.effort.entity.EffortItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EffortItemRepository extends JpaRepository<EffortItem, Long> {
    @Query(value = "SELECT * FROM effort_item e WHERE e.group_id IS NULL OR e.group_id = :groupId ORDER BY e.id ASC", nativeQuery = true)
    List<EffortItem> findDefaultAndCustomByGroupId(@Param("groupId") Long groupId);
}
