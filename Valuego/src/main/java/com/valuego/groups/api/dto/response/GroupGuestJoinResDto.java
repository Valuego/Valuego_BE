package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.Enum.MemberColor;

import java.util.List;

public record GroupGuestJoinResDto(
        Long groupMemberId,
        String memberName,
        MemberColor memberColor,
        GroupInfoResDto group
) {

    public static GroupGuestJoinResDto from(GroupMember groupMember, List<GroupMember> groupMembers) {
        return new GroupGuestJoinResDto(
                groupMember.getId(),
                groupMember.getMemberName(),
                groupMember.getMemberColor(),
                GroupInfoResDto.from(
                        groupMember.getGroup(),
                        groupMembers)
        );
    }
}
