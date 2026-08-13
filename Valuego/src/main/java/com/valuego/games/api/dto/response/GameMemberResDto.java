package com.valuego.games.api.dto.response;

import com.valuego.groups.entity.GroupMember;
import lombok.Builder;

@Builder
public record GameMemberResDto(
        Long groupMemberId,
        String nickname,
        String result
) {
    public static GameMemberResDto from(GroupMember groupMember, String result) {
        return GameMemberResDto.builder()
                .groupMemberId(groupMember.getId())
                .nickname(groupMember.getMemberName())
                .result(result)
                .build();
    }
}
