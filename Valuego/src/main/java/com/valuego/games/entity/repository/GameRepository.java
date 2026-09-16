package com.valuego.games.entity.repository;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByGroupId(Long groupId);

    List<Game> findByGroupIdAndGameType(Long groupId, GameType gameType);
    @Query("SELECT g FROM Game g WHERE g.group.id = :groupId AND DATE(g.createdAt) = :today ORDER BY g.createdAt ASC")
    List<Game> findAllByGroupIdAndCreatedAtDate(@Param("groupId") Long groupId, @Param("today") LocalDate today);
}
