package com.valuego.effort.api.dto.response;

import com.valuego.effort.entity.Effort;

// 수고 회고 응답
public record EffortInfoResDto(
        Long effortId,
        Long writerMemberId,
        Long targetMemberId,
        String targetMemberName,
        Long effortItemId,
        Long effortAmount,
        String comment
) {
    public static EffortInfoResDto from(Effort effort) {
        return new EffortInfoResDto(
                effort.getId(),
                effort.getWriterMember().getId(),
                effort.getTargetMember().getId(),
                effort.getTargetMember().getMemberName(),
                effort.getEffortItem().getId(),
                effort.getEffortAmount(),
                effort.getComment()
        );
    }
}
