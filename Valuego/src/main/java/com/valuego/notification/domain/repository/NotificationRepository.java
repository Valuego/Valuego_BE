package com.valuego.notification.domain.repository;

import com.valuego.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 유저가 받은 모든 그룹의 알림 목록 조회 (통합 알림 창용)
    List<Notification> findByUserIdOrderByNotificationCreatedAtDesc(Long userId);

    // 게스트의 알림 목록 조회
    List<Notification> findByGuestGroupMemberIdOrderByNotificationCreatedAtDesc(Long guestGroupMemberId);
}
