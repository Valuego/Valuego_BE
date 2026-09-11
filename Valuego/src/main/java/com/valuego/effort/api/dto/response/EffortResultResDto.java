package com.valuego.effort.api.dto.response;

import java.util.List;

public record EffortResultResDto(
        Long targetMemberId,
        String targetMemberName,
        Long totalRewardAmount,
        int evaluatorCount,
        List<String> comments
) {
    public static EffortResultResDto of(
            Long targetMemberId,
            String targetMemberName,
            Long totalRewardAmount,
            int evaluatorCount,
            List<String> comments
    ) {
        return new EffortResultResDto(
                targetMemberId,
                targetMemberName,
                totalRewardAmount,
                evaluatorCount,
                comments
        );
    }
}
