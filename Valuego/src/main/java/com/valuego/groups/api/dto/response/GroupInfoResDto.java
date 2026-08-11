package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.Enum.TransportType;

import java.time.LocalDateTime;

public record GroupInfoResDto(
        Long groupId,
        String title,
        Destination destination,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer memberCount,
        TransportType transportType,
        String groupLink
) {
    public static GroupInfoResDto from(Group group) {
        return new GroupInfoResDto(
                group.getId(),
                group.getTitle(),
                group.getDestination(),
                group.getStartDate(),
                group.getEndDate(),
                group.getMemberCount(),
                group.getTransportType(),
                group.getGroupLink()
        );
    }
}
