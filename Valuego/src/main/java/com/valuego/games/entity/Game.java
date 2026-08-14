package com.valuego.games.entity;

import com.valuego.games.api.dto.response.GameMemberInfoResDto;
import com.valuego.global.common.template.BaseTimeEntity;
import com.valuego.groups.entity.Group;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;

    @Column(length = 100)
    private String penalty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<GameMemberInfoResDto> result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Builder
    public Game(GameType gameType, String penalty, List<GameMemberInfoResDto> result, Group group) {
        this.gameType = gameType;
        this.penalty = penalty;
        this.result = result;
        this.group = group;
    }
}
