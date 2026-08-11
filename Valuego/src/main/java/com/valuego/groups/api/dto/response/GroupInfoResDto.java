package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.Enum.TransportType;
import com.valuego.groups.entity.GroupMember;

import java.time.LocalDateTime;
import java.util.List;

public record GroupInfoResDto(
        Long groupId,
        String title,
        Destination destination,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer currentMemberCount,
        Integer memberCount,
        TransportType transportType,
        String groupLink,
        List<GroupMemberInfoResDto> members

) {
    public static GroupInfoResDto from(Group group, List<GroupMember> groupMembers) {
        return new GroupInfoResDto(
                group.getId(),
                group.getTitle(),
                group.getDestination(),
                group.getStartDate(),
                group.getEndDate(),
                groupMembers.size(),
                group.getMemberCount(),
                group.getTransportType(),
                group.getGroupLink(),
                groupMembers.stream()
                        .map(GroupMemberInfoResDto::from)
                        .toList()
        );
    }
}
