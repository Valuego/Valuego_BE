package com.valuego.tourplace.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupStyleDto {
    private String budgetType;
    private String foodType;
    private Integer activityIntensity;
}
