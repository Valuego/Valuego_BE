package com.valuego.comment.entity;

import com.valuego.global.common.template.BaseTimeEntity;
import com.valuego.groups.entity.GroupMember;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_place_id", nullable = false)
    private TravelPlace travelPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id")
    private GroupMember groupMember;

    @Column(nullable = false, length = 500)
    private String content;

    @Builder
    public Comment(TravelPlace travelPlace, User user, GroupMember groupMember, String content) {
        this.travelPlace = travelPlace;
        this.user = user;
        this.groupMember = groupMember;
        this.content = content;
    }
}
