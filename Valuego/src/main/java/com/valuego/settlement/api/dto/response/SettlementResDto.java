package com.valuego.settlement.api.dto.response;

import java.util.List;

public record SettlementResDto(
        Long totalExpense,
        Long expensePerMember,
        Boolean isConfirmed,
        List<EffortRewardResDto> effortRewards,
        List<MemberSettlementResDto> memberSettlements
) {
    // 멤버별 수고 보상 항목
    public record EffortRewardResDto(
            Long groupMemberId,
            String memberName,
            String effortTitle,
            Long rewardAmount
    ) {}

    // 멤버별 최종 정산 항목
    public record MemberSettlementResDto(
            Long groupMemberId,
            String memberName,
            SettlementType settlementType, // SEND(보내요), GIVE(받아요), ZERO(0원)
            Long amount
    ) {}

    public enum SettlementType {
        SEND, // 보내요
        GIVE, // 받아요
        ZERO  // 0원
    }
}