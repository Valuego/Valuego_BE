package com.valuego.expense.entity.repository;

import com.valuego.expense.entity.ExpensePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpensePayerRepository extends JpaRepository<ExpensePayer, Long> {
    @Query("SELECT p FROM ExpensePayer p JOIN FETCH p.expense JOIN FETCH p.groupMember WHERE p.expense.group.id = :groupId")
    List<ExpensePayer> findByGroupIdWithExpense(@Param("groupId") Long groupId);
}
