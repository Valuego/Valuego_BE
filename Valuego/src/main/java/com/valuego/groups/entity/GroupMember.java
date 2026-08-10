package com.valuego.groups.entity;

import com.valuego.global.common.template.BaseTimeEntity;
import com.valuego.groups.entity.Enum.MemberColor;
import com.valuego.groups.entity.Enum.MemberRole;
import com.valuego.groups.entity.Enum.MemberStatus;
import com.valuego.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberColor memberColor;
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    private String memberName;

    // 게스트용 토큰
    @Column(unique = true)
    private String guestToken;
    private LocalDateTime guestTokenExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Builder
    public GroupMember(MemberColor memberColor, MemberRole memberRole, MemberStatus memberStatus, String memberName, String guestToken, LocalDateTime guestTokenExpiresAt, User user, Group group) {
        this.memberColor = memberColor;
        this.memberRole = memberRole;
        this.memberStatus = memberStatus;
        this.memberName = memberName;
        this.guestToken = guestToken;
        this.user = user;
        this.guestTokenExpiresAt = guestTokenExpiresAt;
        this.group = group;
    }
}
