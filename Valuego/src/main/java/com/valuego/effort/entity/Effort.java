package com.valuego.effort.entity;

import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Effort {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "effort_id")
    private Long id;

    private Long effortAmount;
    private String comment;

    @Enumerated(EnumType.STRING)
    private EffortType effortType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_member_id", nullable = false)
    private GroupMember writerMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id", nullable = false)
    private GroupMember targetMember;

    @Builder
    public Effort(Long effortAmount, String comment, EffortType effortType, Group group, GroupMember writerMember, GroupMember targetMember) {
        this.effortAmount = effortAmount;
        this.comment = comment;
        this.effortType = effortType;
        this.group = group;
        this.writerMember = writerMember;
        this.targetMember = targetMember;
    }
}
