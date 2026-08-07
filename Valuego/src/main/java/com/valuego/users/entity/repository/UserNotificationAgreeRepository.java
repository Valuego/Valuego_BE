package com.valuego.users.entity.repository;

import com.valuego.users.entity.User;
import com.valuego.users.entity.UserNotificationAgree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationAgreeRepository extends JpaRepository<UserNotificationAgree, Long> {
    Optional<UserNotificationAgree> findByUser(User user);
}
