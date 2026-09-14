package com.valuego.effort.entity.repository;

import com.valuego.effort.entity.Effort;
import com.valuego.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EffortRepository extends JpaRepository<Effort, Long> {
    List<Effort> findByGroupIdAndTargetMemberId(Long groupId, Long targetMemberId);

    @Query("SELECT e FROM Effort e JOIN FETCH e.effortItem JOIN FETCH e.targetMember WHERE e.group.id = :groupId")
    List<Effort> findByGroupIdWithItemAndTargetMember(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(DISTINCT e.writerMember) FROM Effort e WHERE e.group = :group")
    long countDistinctWriterMemberByGroup(@Param("group") Group group);
}
