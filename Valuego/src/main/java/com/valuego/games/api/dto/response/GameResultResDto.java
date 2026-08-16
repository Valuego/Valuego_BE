package com.valuego.games.api.dto.response;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import lombok.Builder;

import java.util.List;

@Builder
public record GameResultResDto(
        Long gameId,
        Long groupId,
        GameType gameType,
        String penalty,
        List<GameMemberInfoResDto> result
) {
    public static GameResultResDto from(Game game) {
        return GameResultResDto.builder()
                .gameId(game.getId())
                .groupId(game.getGroup().getId())
                .gameType(game.getGameType())
                .penalty(game.getPenalty())
                .result(game.getResult())
                .build();
    }
}
