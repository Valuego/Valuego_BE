package com.valuego.votes.api.dto.response;

import com.valuego.votes.entity.VoteStatus;
import lombok.Builder;

@Builder
public record VoteResDto(
        long likeCount,
        int likePercentage,
        long dislikeCount,
        int dislikePercentage,
        long totalParticipantCount,
        int totalGroupMemberCount,
        VoteStatus voteStatus
) {
    public static VoteResDto of(
            long likeCount,
            long dislikeCount,
            int totalGroupMemberCount,
            VoteStatus voteStatus
    ) {
        long totalParticipantCount = likeCount + dislikeCount;
        int likePercentage = totalParticipantCount == 0 ? 0 : (int) Math.round(((double) likeCount / totalParticipantCount) * 100);
        int dislikePercentage = totalParticipantCount == 0 ? 0 : 100 - likePercentage;

        return VoteResDto.builder()
                .likeCount(likeCount)
                .likePercentage(likePercentage)
                .dislikeCount(dislikeCount)
                .dislikePercentage(dislikePercentage)
                .totalParticipantCount(totalParticipantCount)
                .totalGroupMemberCount(totalGroupMemberCount)
                .voteStatus(voteStatus)
                .build();
    }
}
