package com.valuego.groups.entity.repository;

import com.valuego.groups.entity.Group;
import com.valuego.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByGroupLink(String groupLink);
    List<Group> findAllByLeaderOrderByStartDateDesc(User leader);

}
