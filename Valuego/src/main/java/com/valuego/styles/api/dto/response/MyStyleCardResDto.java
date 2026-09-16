package com.valuego.styles.api.dto.response;

import com.valuego.styles.entity.Enum.BudgetType;
import com.valuego.styles.entity.Enum.FoodType;
import lombok.Builder;

import java.util.List;

@Builder
public record MyStyleCardResDto(
        Long styleId,
        Long groupId,
        Long groupMemberId,
        String dnaTitle,
        String dnaDescription,
        List<String> tags,
        String activityLevelText,
        String budgetStyleText,
        String preferredFoodText,
        BudgetType budgetType,
        FoodType foodType,
        Integer activityIntensity
) {
    public static MyStyleCardResDto of(
            Long styleId,
            Long groupId,
            Long groupMemberId,
            String dnaTitle,
            String dnaDescription,
            List<String> tags,
            String activityLevelText,
            String budgetStyleText,
            String preferredFoodText,
            BudgetType budgetType,
            FoodType foodType,
            Integer activityIntensity
    ) {
        return MyStyleCardResDto.builder()
                .styleId(styleId)
                .groupId(groupId)
                .groupMemberId(groupMemberId)
                .dnaTitle(dnaTitle)
                .dnaDescription(dnaDescription)
                .tags(tags)
                .activityLevelText(activityLevelText)
                .budgetStyleText(budgetStyleText)
                .preferredFoodText(preferredFoodText)
                .budgetType(budgetType)
                .foodType(foodType)
                .activityIntensity(activityIntensity)
                .build();
    }
}
