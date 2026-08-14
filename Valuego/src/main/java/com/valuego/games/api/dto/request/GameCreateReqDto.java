package com.valuego.games.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameCreateReqDto(
        @NotBlank(message = "벌칙을 입력해주세요.")
        @Size(max = 100, message = "벌칙은 100자 이하로 입력해주세요.")
        String penalty
) {
}
