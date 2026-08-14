package com.valuego.games.quiz.dto.response;

import java.util.List;

public record QuizAiResDto(
        String question,
        List<QuizOptionInfoResDto> options,
        String correctOption,
        String explanation
) {
}
