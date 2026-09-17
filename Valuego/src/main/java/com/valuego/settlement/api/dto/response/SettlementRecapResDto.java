package com.valuego.settlement.api.dto.response;

import com.valuego.expense.api.dto.response.ExpenseInfoResDto;

import java.math.BigDecimal;
import java.util.List;

public record SettlementRecapResDto(
        Long groupId,
        String groupTitle,
        String groupPeriod,
        String durationText,
        int memberCount,
        String totalDistance,
        BigDecimal totalExpenseAmount,
        String gameResult,
        BigDecimal totalEffortAmount
) {
    public static SettlementRecapResDto of(
            Long groupId,
            String groupTitle,
            String groupPeriod,
            String durationText,
            int memberCount,
            String totalDistance,
            BigDecimal totalExpenseAmount,
            String gameResult,
            BigDecimal totalEffortAmount
    ) {
        return new SettlementRecapResDto(
                groupId,
                groupTitle,
                groupPeriod,
                durationText,
                memberCount,
                totalDistance,
                totalExpenseAmount,
                gameResult,
                totalEffortAmount
        );
    }
}
