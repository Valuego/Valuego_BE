package com.valuego.groups.entity.repository;

import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    long countByGroup(Group group);
    Optional<GroupMember> findByGuestToken(String guestToken);
    List<GroupMember> findAllByGroup(Group group);
    List<GroupMember> findAllByGroupIn(List<Group> groups);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    List<GroupMember> findAllByGroupId(Long groupId);
}
