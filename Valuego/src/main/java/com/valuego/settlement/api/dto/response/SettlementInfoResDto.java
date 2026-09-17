package com.valuego.settlement.api.dto.response;

import com.valuego.settlement.entity.Settlement;

public record SettlementInfoResDto(
        Long settlementId,
        Long groupId,
        Boolean isConfirmed
) {
    public static SettlementInfoResDto from(Settlement settlement) {
        return new SettlementInfoResDto(
                settlement.getId(),
                settlement.getGroup().getId(),
                settlement.getIsConfirmed()
        );
    }
}
