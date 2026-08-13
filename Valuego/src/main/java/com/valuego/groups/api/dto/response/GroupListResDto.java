package com.valuego.groups.api.dto.response;

import java.util.List;

public record GroupListResDto(
        List<GroupInfoResDto> ongoingGroups,
        List<GroupInfoResDto> pastGroups
) {
}
