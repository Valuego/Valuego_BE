package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Enum.GroupStatus;
import com.valuego.groups.entity.Group;

import java.time.LocalDateTime;

public record GroupStatusResDto(
        Long groupId,
        String title,
        Destination destination,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String groupLink,
        GroupStatus groupStatus

) {
    public static GroupStatusResDto from(Group group) {
        return new GroupStatusResDto(
                group.getId(),
                group.getTitle(),
                group.getDestination(),
                group.getStartDate(),
                group.getEndDate(),
                group.getGroupLink(),
                group.getGroupStatus()
        );
    }
}
