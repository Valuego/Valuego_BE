package com.valuego.votes.api.dto.request;

import com.valuego.votes.entity.VoteStatus;
import jakarta.validation.constraints.NotNull;

public record VoteReqDto(
        @NotNull(message = "투표 상태(LIKE 또는 DISLIKE)를 입력해주세요.")
        VoteStatus voteStatus
) {
}
