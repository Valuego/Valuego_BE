package com.valuego.notification.api.dto.response;

import com.valuego.notification.domain.Notification;
import com.valuego.notification.domain.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResDto(
        Long notificationId,
        Long userId,
        Long guestGroupMemberId,
        NotificationType type,
        String title,
        String content,
        Long targetId,
        boolean isRead,
        LocalDateTime notificationCreatedAt,
        Long totalCount
) {
    public static NotificationResDto from(Notification notification, Long totalCount) {
        return NotificationResDto.builder()
                .notificationId(notification.getId())
                .userId(notification.getUserId())
                .guestGroupMemberId(notification.getGuestGroupMemberId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .targetId(notification.getGroupId())
                .isRead(notification.isRead())
                .notificationCreatedAt(notification.getNotificationCreatedAt())
                .totalCount(totalCount)
                .build();
    }

    public static NotificationResDto from(Notification notification) {
        return from(notification, null);
    }
}
