package com.valuego.games.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;

// 퀴즈 답 요청 dto
public record QuizAnswerReqDto(
        @NotBlank
        String selectedOption
) {
}
