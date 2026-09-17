package com.valuego.settlement.entity;

import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "settlements")
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "settlement_confirmed_members", joinColumns = @JoinColumn(name = "settlement_id"))
    @Column(name = "group_member_id")
    @Builder.Default
    private Set<Long> confirmedMemberIds = new HashSet<>();

    public boolean getIsConfirmed() {
        return this.isConfirmed;
    }

    public void confirmMember(Long groupMemberId, int totalMemberCount, Long totalExpense, Long expensePerMember) {
        this.totalExpense = totalExpense;
        this.expensePerMember = expensePerMember;
        this.confirmedMemberIds.add(groupMemberId);

        // 그룹 전체 멤버가 모두 확인을 누르면 전원 확인 완료(isConfirmed = true)
        if (this.confirmedMemberIds.size() >= totalMemberCount) {
            this.isConfirmed = true;
            this.confirmedAt = LocalDateTime.now();
        }
    }
}