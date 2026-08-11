package com.valuego.styles.entity;

import com.valuego.groups.entity.GroupMember;
import com.valuego.styles.entity.Enum.BudgetType;
import com.valuego.styles.entity.Enum.FoodType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "style_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false, unique = true)
    private GroupMember groupMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetType budgetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodType foodType;

    @Column(nullable = false)
    private Integer activityIntensity;

    public Style(GroupMember groupMember, BudgetType budgetType, FoodType foodType, Integer activityIntensity) {
        this.groupMember = groupMember;
        this.budgetType = budgetType;
        this.foodType = foodType;
        this.activityIntensity = activityIntensity;
    }
}
