package com.valuego.effort.entity;

import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EffortItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private String title;

    private Boolean isCustom;

    @Builder
    public EffortItem(Group group, String title, Boolean isCustom) {
        this.group = group;
        this.title = title;
        this.isCustom = isCustom;
    }

    public static EffortItem createCustomItem(Group group, String title) {
        return EffortItem.builder()
                .group(group)
                .title(title)
                .isCustom(true)
                .build();
    }
}
