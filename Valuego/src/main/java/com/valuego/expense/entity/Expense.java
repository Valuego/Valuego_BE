package com.valuego.expense.entity;

import com.valuego.expense.entity.Enum.ExpenseCategory;
import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Expense {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private String title;
    private BigDecimal amount;
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Builder.Default
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpensePayer> expensePayers = new ArrayList<>();

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpenseParticipant> expenseParticipants = new ArrayList<>();

    @Builder
    public Expense(Group group, String title, BigDecimal amount, LocalDate expenseDate) {
        this.group = group;
        this.title = title;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }
}
