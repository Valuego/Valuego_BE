package com.valuego.expense.api.dto.response;

import com.valuego.expense.entity.Expense;
import com.valuego.expense.entity.Enum.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseInfoResDto(
        Long expenseId,
        BigDecimal amount,
        ExpenseCategory category,
        LocalDate expenseDate,
        String payerName,
        int participantCount,
        List<ExpensePayerResDto> payers,
        List<ExpenseParticipantResDto> participants
) {
    public static ExpenseInfoResDto from(Expense expense) {
        String payerName = "default";
        if (expense.getExpensePayers() != null && !expense.getExpensePayers().isEmpty()) {
            payerName = expense.getExpensePayers().get(0).getGroupMember().getMemberName();
        }

        int participantCount = 0;
        if (expense.getExpenseParticipants() != null) {
            participantCount = (int) expense.getExpenseParticipants().stream()
                    .filter(p -> p.getIsIncluded() != null && p.getIsIncluded())
                    .count();
        }

        List<ExpensePayerResDto> payerDtos = expense.getExpensePayers().stream()
                .map(p -> new ExpensePayerResDto(p.getId(), p.getGroupMember().getId()))
                .toList();

        List<ExpenseParticipantResDto> participantDtos = expense.getExpenseParticipants().stream()
                .map(p -> new ExpenseParticipantResDto(p.getId(), p.getGroupMember().getId(), p.getIsIncluded()))
                .toList();

        return new ExpenseInfoResDto(
                expense.getId(),
                expense.getAmount(),
                expense.getExpenseCategory(),
                expense.getExpenseDate(),
                payerName,
                participantCount,
                payerDtos,
                participantDtos
        );
    }

    public record ExpensePayerResDto(Long expensePayerId, Long groupMemberId) {}
    public record ExpenseParticipantResDto(Long expenseParticipantId, Long groupMemberId, Boolean isIncluded) {}
}
