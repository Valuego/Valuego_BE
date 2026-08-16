package com.valuego.games.api.dto.response;

import com.valuego.groups.entity.GroupMember;

public record GameMemberListResDto(
        Long groupMemberId,
        String memberName
) {
    public static GameMemberListResDto from(GroupMember groupMember) {
        return new GameMemberListResDto(
                groupMember.getId(),
                groupMember.getMemberName()
        );
    }
}
