package com.valuego.effort.api.dto.response;

import com.valuego.effort.entity.EffortItem;
import com.valuego.games.api.dto.response.GameMemberListResDto;

import java.util.List;

public record EffortItemResDto(
        Long effortItemId,
        String title,
        Boolean isCustom,
        List<GameMemberListResDto> memberList
) {
    public static EffortItemResDto from(EffortItem item) {
        return new EffortItemResDto(
                item.getId(),
                item.getTitle(),
                item.getIsCustom(),
                null
        );
    }

    public static EffortItemResDto of(EffortItem item, List<GameMemberListResDto> memberList) {
        return new EffortItemResDto(
                item.getId(),
                item.getTitle(),
                item.getIsCustom(),
                memberList
        );
    }
}
