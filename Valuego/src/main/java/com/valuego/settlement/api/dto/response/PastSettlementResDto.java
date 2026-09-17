package com.valuego.settlement.api.dto.response;

import java.math.BigDecimal;

public record PastSettlementResDto(
        Long groupId,
        Long settlementId,
        String groupTitle,
        String groupPeriod,
        BigDecimal totalExpense,
        BigDecimal expensePerMember
) {
    public static PastSettlementResDto of(
            Long groupId,
            Long settlementId,
            String groupTitle,
            String groupPeriod,
            BigDecimal totalExpense,
            BigDecimal expensePerMember
    ) {
        return new PastSettlementResDto(
                groupId,
                settlementId,
                groupTitle,
                groupPeriod,
                totalExpense,
                expensePerMember
        );
    }
}
