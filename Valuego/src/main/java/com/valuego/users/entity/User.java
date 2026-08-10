package com.valuego.users.entity;

import com.valuego.global.common.template.BaseTimeEntity;
import com.valuego.groups.entity.Enum.MemberColor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private MemberColor memberColor;

    private String refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserNotificationAgree userNotificationAgree;

    @Builder
    public User(String nickname, String email, String profileImageUrl, SocialType socialType, UserRole userRole, MemberColor memberColor, String refreshToken, UserNotificationAgree userNotificationAgree) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.socialType = socialType;
        this.userRole = userRole;
        this.memberColor = memberColor;
        this.refreshToken = refreshToken;
        this.userNotificationAgree = userNotificationAgree;
    }

    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateMemberColor(MemberColor memberColor) {
        this.memberColor = memberColor;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
