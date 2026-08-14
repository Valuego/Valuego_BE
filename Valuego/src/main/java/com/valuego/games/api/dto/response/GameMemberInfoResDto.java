package com.valuego.games.api.dto.response;

import com.valuego.groups.entity.GroupMember;
import lombok.Builder;

@Builder
public record GameMemberInfoResDto(
        Long groupMemberId,
        String nickname,
        String result
) {
    public static GameMemberInfoResDto from(GroupMember groupMember, String result) {
        return GameMemberInfoResDto.builder()
                .groupMemberId(groupMember.getId())
                .nickname(groupMember.getMemberName())
                .result(result)
                .build();
    }
}
