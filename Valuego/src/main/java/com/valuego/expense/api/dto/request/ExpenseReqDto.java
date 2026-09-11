package com.valuego.expense.api.dto.request;

import com.valuego.expense.entity.Enum.ExpenseCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 지출 생성 요청
public record ExpenseReqDto(
        @NotNull
        Long groupId,
        @NotNull
        BigDecimal amount,
        ExpenseCategory category,
        LocalDate expenseDate,
        @NotNull
        List<ExpensePayerReqDto> payers,
        @NotNull
        List<ExpenseParticipantReqDto> participants
) {
    public static ExpenseReqDto of(
            Long groupId,
            BigDecimal amount,
            ExpenseCategory category,
            LocalDate expenseDate,
            List<ExpensePayerReqDto> payers,
            List<ExpenseParticipantReqDto> participants
    ) {
        return new ExpenseReqDto(groupId, amount, category, expenseDate, payers, participants);
    }
}
