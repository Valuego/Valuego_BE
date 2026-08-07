package com.valuego.users.entity.repository;

import com.valuego.users.entity.UserNotificationAgree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationAgreeRepository extends JpaRepository<UserNotificationAgree, Long> {

}
