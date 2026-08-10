package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.Enum.MemberColor;

public record GroupGuestJoinResDto(
        Long groupMemberId,
        String memberName,
        MemberColor memberColor,
        String guestToken,
        GroupInfoResDto group
) {

    public static GroupGuestJoinResDto from(GroupMember groupMember) {
        return new GroupGuestJoinResDto(
                groupMember.getId(),
                groupMember.getMemberName(),
                groupMember.getMemberColor(),
                groupMember.getGuestToken(),
                GroupInfoResDto.from(groupMember.getGroup())
        );
    }
}
