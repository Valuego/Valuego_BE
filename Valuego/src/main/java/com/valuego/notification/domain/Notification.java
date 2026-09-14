package com.valuego.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long userId;

    private Long guestGroupMemberId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String content;

    private Long groupId;

    private boolean isRead;

    private LocalDateTime notificationCreatedAt;

    @PrePersist
    public void prePersist() {
        if (notificationCreatedAt == null) {
            notificationCreatedAt = LocalDateTime.now();
        }
    }

    public void read() {
        this.isRead = true;
    }
}
