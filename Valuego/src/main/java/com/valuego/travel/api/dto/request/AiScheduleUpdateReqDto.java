package com.valuego.travel.api.dto.request;

public record AiScheduleUpdateReqDto(
        Long groupId,
        int dayNum,
        String prompt
) {
}
