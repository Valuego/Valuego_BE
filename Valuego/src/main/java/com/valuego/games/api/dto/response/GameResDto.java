package com.valuego.games.api.dto.response;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import lombok.Builder;

import java.util.List;

@Builder
public record GameResDto(
        Long gameId,
        Long groupId,
        GameType gameType,
        String penalty,
        List<GameMemberResDto> result
) {
    public static GameResDto from(Game game) {
        return GameResDto.builder()
                .gameId(game.getId())
                .groupId(game.getGroup().getId())
                .gameType(game.getGameType())
                .penalty(game.getPenalty())
                .result(game.getResult())
                .build();
    }
}
