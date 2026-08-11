package com.valuego.groups.api.dto.response;

import com.valuego.groups.entity.Enum.MemberColor;
import com.valuego.groups.entity.Enum.MemberRole;
import com.valuego.groups.entity.Enum.MemberStatus;
import com.valuego.groups.entity.GroupMember;

public record GroupMemberInfoResDto(
        Long groupMemberId,
        String memberName,
        MemberColor memberColor,
        MemberRole memberRole,
        MemberStatus memberStatus
) {

    public static GroupMemberInfoResDto from(GroupMember groupMember) {
        return new GroupMemberInfoResDto(
                groupMember.getId(),
                groupMember.getMemberName(),
                groupMember.getMemberColor(),
                groupMember.getMemberRole(),
                groupMember.getMemberStatus()
        );
    }
}