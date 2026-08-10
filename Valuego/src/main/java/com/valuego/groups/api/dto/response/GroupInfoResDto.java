package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.Enum.TransportType;

import java.time.LocalDateTime;

public record GroupInfoResDto(
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
