package com.valuego.games.quiz.dto.response;

import com.valuego.games.quiz.entity.LocalQuiz;

import java.util.List;

public record QuizOptionListResDto(
        Long gameId,
        Long groupId,
        String question,
        List<QuizOptionInfoResDto> options
) {

    public static QuizOptionListResDto from(Long gameId, Long groupId, LocalQuiz quiz) {
        return new QuizOptionListResDto(
                gameId,
                groupId,
                quiz.getQuestion(),
                List.of(
                        new QuizOptionInfoResDto("A", quiz.getOptionA()),
                        new QuizOptionInfoResDto("B", quiz.getOptionB()),
                        new QuizOptionInfoResDto("C", quiz.getOptionC()),
                        new QuizOptionInfoResDto("D", quiz.getOptionD())
                )
        );
    }
}
