package com.valuego.effort.api.dto.request;

import jakarta.validation.constraints.NotNull;

// 수고 회고 생성
public record EffortReqDto(
        @NotNull
        Long groupId,
        Long targetMemberId,
        Long effortAmount,
        String comment,
        @NotNull
        Long effortItemId
) {
    public static EffortReqDto of(
            Long groupId,
            Long targetMemberId,
            Long effortAmount,
            String comment,
            Long effortItemId
    ) {
        return new EffortReqDto(groupId, targetMemberId, effortAmount, comment, effortItemId);
    }
}
