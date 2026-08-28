package com.valuego.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationAgree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "notify_comments", nullable = false)
    private boolean notifyComments = true; // 투표, 의견 알림

    @Column(name = "notify_reminders", nullable = false)
    private boolean notifyReminders = true; // 일정 리마인드

    @Column(name = "notify_settlement", nullable = false)
    private boolean notifySettlement = true; // 정산 알림

    @Column(name = "notify_marketing", nullable = false)
    private boolean notifyMarketing = false; // 혜택·마케팅 알림

    @Builder
    public UserNotificationAgree(User user, boolean notifyComments, boolean notifyReminders, boolean notifySettlement, boolean notifyMarketing) {
        this.user = user;
        this.notifyComments = notifyComments;
        this.notifyReminders = notifyReminders;
        this.notifySettlement = notifySettlement;
        this.notifyMarketing = notifyMarketing;
    }

    public void updateNotificationAgree(boolean notifyComments, boolean notifyReminders, boolean notifySettlement, boolean notifyMarketing) {
        this.notifyComments = notifyComments;
        this.notifyReminders = notifyReminders;
        this.notifySettlement = notifySettlement;
        this.notifyMarketing = notifyMarketing;
    }

    public static UserNotificationAgree createUserAgree(User user) {
        return UserNotificationAgree.builder()
                .user(user)
                .notifyComments(true)
                .notifyReminders(true)
                .notifySettlement(true)
                .notifyMarketing(true)
                .build();
    }
}
