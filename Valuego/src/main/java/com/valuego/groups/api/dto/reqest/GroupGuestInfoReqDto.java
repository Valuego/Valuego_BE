package com.valuego.groups.api.dto.reqest;

import com.valuego.groups.entity.Enum.MemberColor;

public record GroupGuestInfoReqDto(
        String memberName,
        MemberColor memberColor
) {
}
