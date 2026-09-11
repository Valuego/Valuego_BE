package com.valuego.expense.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record ExpenseParticipantReqDto(
        Long groupMemberId,
        @NotNull
        Boolean isIncluded
) {
}
