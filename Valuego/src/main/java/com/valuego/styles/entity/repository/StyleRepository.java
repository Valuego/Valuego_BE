package com.valuego.styles.entity.repository;

import com.valuego.groups.entity.GroupMember;
import com.valuego.styles.entity.Style;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleRepository extends JpaRepository<Style, Long> {
    boolean existsByGroupMember(GroupMember groupMember);
}
