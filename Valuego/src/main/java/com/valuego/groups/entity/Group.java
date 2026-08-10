package com.valuego.groups.entity;

import com.valuego.global.common.template.BaseTimeEntity;
import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Enum.TransportType;
import com.valuego.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "travel_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    private String title;

    @Enumerated(EnumType.STRING)
    private Destination destination;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer memberCount;
    private String groupLink;

    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @Builder
    public Group(String title, Destination destination, LocalDateTime startDate, LocalDateTime endDate, Integer memberCount, String groupLink, TransportType transportType, User leader) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memberCount = memberCount;
        this.groupLink = groupLink;
        this.transportType = transportType;
        this.leader = leader;
    }
}
