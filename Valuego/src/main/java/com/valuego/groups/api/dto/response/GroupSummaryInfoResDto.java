package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Group;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GroupSummaryInfoResDto(
        String inviterName,
        String title,
        Destination destination,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int memberCount
) {
    public static GroupSummaryInfoResDto from(Group group) {
        return GroupSummaryInfoResDto.builder()
                .inviterName(group.getLeader().getNickname())
                .title(group.getTitle())
                .destination(group.getDestination())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .memberCount(group.getMemberCount())
                .build();
    }
}
