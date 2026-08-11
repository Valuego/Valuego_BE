package com.valuego.users.api.dto.request;

import com.valuego.groups.entity.Enum.MemberColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserInfoUpdateReqDto(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,
        @NotNull(message = "프로필 색상은 필수입니다. BLUE, PURPLE, ORANGE, SKYBLUE")
        MemberColor memberColor
) {
}
