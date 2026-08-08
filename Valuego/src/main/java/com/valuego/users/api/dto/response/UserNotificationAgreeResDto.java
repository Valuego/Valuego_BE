package com.valuego.users.api.dto.response;

import com.valuego.users.entity.UserNotificationAgree;
import lombok.Builder;

@Builder
public record UserNotificationAgreeResDto(
        boolean notifyComments,
        boolean notifyReminders,
        boolean notifySettlement,
        boolean notifyMarketing
) {
    public static UserNotificationAgreeResDto from(UserNotificationAgree agree) {
        if (agree == null) return null;
        return UserNotificationAgreeResDto.builder()
                .notifyComments(agree.isNotifyComments())
                .notifyReminders(agree.isNotifyReminders())
                .notifySettlement(agree.isNotifySettlement())
                .notifyMarketing(agree.isNotifyMarketing())
                .build();
    }
}
