package com.valuego.expense.entity.repository;

import com.valuego.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroupId(Long groupId);
    List<Expense> findAllByGroupIdAndExpenseDate(Long groupId, LocalDate expenseDate);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.group.id = :groupId AND e.expenseDate = :expenseDate")
    BigDecimal findTotalAmountByGroupIdAndExpenseDate(@Param("groupId") Long groupId, @Param("expenseDate") LocalDate expenseDate);
}
