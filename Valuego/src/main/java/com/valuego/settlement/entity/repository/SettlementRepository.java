package com.valuego.settlement.entity.repository;

import com.valuego.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByGroupId(Long groupId);
    @Query("SELECT s FROM Settlement s JOIN s.group g " +
            "WHERE g.leader.id = :memberId AND s.isConfirmed = true " +
            "ORDER BY g.startDate DESC")
    List<Settlement> findConfirmedSettlementsByLeaderId(@Param("memberId") Long memberId);
}
