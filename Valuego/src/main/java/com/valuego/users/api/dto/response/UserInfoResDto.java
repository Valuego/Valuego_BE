package com.valuego.users.api.dto.response;

import com.valuego.groups.entity.Enum.MemberColor;
import com.valuego.users.entity.SocialType;
import com.valuego.users.entity.User;
import com.valuego.users.entity.UserRole;
import lombok.Builder;

@Builder
public record UserInfoResDto(
        Long userId,
        String nickname,
        String email,
        String profileImageUrl,
        SocialType socialType,
        UserRole userRole,
        MemberColor memberColor,
        UserNotificationAgreeResDto notificationAgree
) {
    public static UserInfoResDto from(User user) {
        return UserInfoResDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .socialType(user.getSocialType())
                .userRole(user.getUserRole())
                .memberColor(user.getMemberColor())
                .notificationAgree(UserNotificationAgreeResDto.from(user.getUserNotificationAgree()))
                .build();
    }
}
