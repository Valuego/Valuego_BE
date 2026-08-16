package com.valuego.games.quiz.dto.response;

// 정답 판별 RES
public record QuizAnswerResDto(
        boolean correct,
        String selectedOption,
        String correctOption,
        String explanation
) {
}
