package com.valuego.settlement.entity;

import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    private Group group;

    @Column(nullable = false)
    private Long totalExpense;

    @Column(nullable = false)
    private Long expensePerMember;

    @Builder.Default
    @Column(nullable = false)
    private boolean isConfirmed = false;

    private LocalDateTime confirmedAt;

    public boolean getIsConfirmed() {
        return this.isConfirmed;
    }

    public void confirmSettlement(Long totalExpense, Long expensePerMember) {
        this.totalExpense = totalExpense;
        this.expensePerMember = expensePerMember;
        this.isConfirmed = true;
        this.confirmedAt = LocalDateTime.now();
    }
}