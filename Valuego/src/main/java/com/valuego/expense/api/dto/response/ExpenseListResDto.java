package com.valuego.expense.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseListResDto(
        BigDecimal totalAmount,
        List<ExpenseInfoResDto> expenseInfoResDtos
) {
    public static ExpenseListResDto of(BigDecimal totalAmount, List<ExpenseInfoResDto> expenseInfoResDtos) {
        return new ExpenseListResDto(totalAmount, expenseInfoResDtos);
    }
}
