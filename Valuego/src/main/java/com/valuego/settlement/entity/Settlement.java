package com.valuego.settlement.entity;

import com.valuego.groups.entity.Group;
import com.valuego.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Settlement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    private Boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Settlement(Boolean isApproved, Group group, User user) {
        this.isApproved = isApproved;
        this.group = group;
        this.user = user;
    }
}
