package com.valuego.users.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserAgreeUpdateReqDto(
        @NotNull
        boolean notifyComments,
        @NotNull
        boolean notifyReminders,
        @NotNull
        boolean notifySettlement,
        @NotNull
        boolean notifyMarketing
) {
}
