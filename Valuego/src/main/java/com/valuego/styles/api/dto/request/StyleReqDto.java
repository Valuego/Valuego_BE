package com.valuego.styles.api.dto.request;

import com.valuego.styles.entity.Enum.BudgetType;
import com.valuego.styles.entity.Enum.FoodType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StyleReqDto(

        @NotNull
        BudgetType budgetType,

        @NotNull
        FoodType foodType,

        @NotNull
        @Min(1)
        @Max(5)
        Integer activityIntensity
) {
}