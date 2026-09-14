package com.valuego.notification.api.dto.response;

import com.valuego.notification.domain.NotificationType;

import java.util.List;

public record NotificationEvent(
        List<Long> targetGroupMemberIds,
        NotificationType type,
        Long targetId
) {}
