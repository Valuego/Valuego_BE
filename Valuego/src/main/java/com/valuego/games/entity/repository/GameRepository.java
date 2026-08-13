package com.valuego.games.entity.repository;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByGroupId(Long groupId);

    List<Game> findByGroupIdAndGameType(Long groupId, GameType gameType);
}