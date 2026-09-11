package com.valuego.expense.service;

import com.valuego.expense.api.dto.request.ExpenseReqDto;
import com.valuego.expense.api.dto.response.ExpenseInfoResDto;
import com.valuego.expense.api.dto.response.ExpenseListResDto;
import com.valuego.expense.entity.Expense;
import com.valuego.expense.entity.ExpenseParticipant;
import com.valuego.expense.entity.ExpensePayer;
import com.valuego.expense.entity.repository.ExpenseRepository;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;

    // 지출 기록 생성
    @Transactional
    public ExpenseInfoResDto createExpense(Principal principal, ExpenseReqDto expenseReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(expenseReqDto.groupId());
        validMemberException.validateGroupMember(principal, guestToken, group);

        // expense 생성
        Expense expense = Expense.builder()
                .group(group)
                .amount(expenseReqDto.amount())
                .expenseCategory(expenseReqDto.category())
                .expenseDate(expenseReqDto.expenseDate())
                .build();

        // 결제자 payers 매핑
        if (expenseReqDto.payers() != null) {
            for (var pDto : expenseReqDto.payers()) {
                GroupMember groupMember = entityFinderException.getGroupMemberByIdAndGroup(pDto.groupMemberId(), group.getId());

                ExpensePayer payer = ExpensePayer.builder()
                        .expense(expense)
                        .groupMember(groupMember)
                        .build();

                expense.getExpensePayers().add(payer);
            }
        }

        // 참여자 participants 매핑
        if (expenseReqDto.participants() != null) {
            for (var pDto : expenseReqDto.participants()) {
                GroupMember groupMember = entityFinderException.getGroupMemberByIdAndGroup(pDto.groupMemberId(), group.getId());

                ExpenseParticipant participant = ExpenseParticipant.builder()
                        .expense(expense)
                        .groupMember(groupMember)
                        .isIncluded(pDto.isIncluded())
                        .build();

                expense.getExpenseParticipants().add(participant);
            }
        }

        Expense savedExpense = expenseRepository.save(expense);

        return ExpenseInfoResDto.from(savedExpense);
    }

    // 지출 기록 전체 조회
    public ExpenseListResDto getAllExpenses(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        List<ExpenseInfoResDto> expenseDtos = expenses.stream()
                .map(ExpenseInfoResDto::from)
                .toList();

        BigDecimal totalSpentAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ExpenseListResDto.of(totalSpentAmount, expenseDtos);
    }
}
