package com.valuego.votes.entity;

import com.valuego.global.common.template.BaseTimeEntity;
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
@Table(name = "travel_vote")
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_place_id", nullable = false)
    private TravelPlace travelPlace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus voteStatus;

    @Builder
    public Vote(User user, TravelPlace travelPlace, VoteStatus voteStatus) {
        this.user = user;
        this.travelPlace = travelPlace;
        this.voteStatus = voteStatus;
    }

    public void updateStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }
}
