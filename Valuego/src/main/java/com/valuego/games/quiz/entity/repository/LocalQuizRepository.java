package com.valuego.games.quiz.entity.repository;

import com.valuego.games.entity.Game;
import com.valuego.games.quiz.entity.LocalQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalQuizRepository extends JpaRepository<LocalQuiz, Long> {
    Optional<LocalQuiz> findByGame(Game game);
}
