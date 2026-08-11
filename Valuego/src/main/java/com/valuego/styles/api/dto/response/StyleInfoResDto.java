package com.valuego.styles.api.dto.response;

import com.valuego.styles.entity.Enum.BudgetType;
import com.valuego.styles.entity.Enum.FoodType;
import com.valuego.styles.entity.Style;

public record StyleInfoResDto(
        Long styleId,
        Long groupId,
        Long groupMemberId,
        BudgetType budgetType,
        FoodType foodType,
        Integer activityIntensity
) {

    public static StyleInfoResDto from(Style style) {
        return new StyleInfoResDto(
                style.getId(),
                style.getGroupMember().getGroup().getId(),
                style.getGroupMember().getId(),
                style.getBudgetType(),
                style.getFoodType(),
                style.getActivityIntensity()
        );
    }
}
