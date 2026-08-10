package com.valuego.groups.api.dto.reqest;

import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Enum.TransportType;

import java.time.LocalDateTime;

public record GroupCreateReqDto(
        String title,
        Destination destination,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer memberCount,
        TransportType transportType
) {
}
