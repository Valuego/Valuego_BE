package com.valuego.styles.api.dto.response;

import com.valuego.groups.entity.Group;

import java.time.LocalDateTime;

public record StyleGroupListResDto(
        Long groupId,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public static StyleGroupListResDto from(Group group) {
        return new StyleGroupListResDto(
                group.getId(),
                group.getTitle(),
                group.getStartDate(),
                group.getEndDate()
        );
    }
}
