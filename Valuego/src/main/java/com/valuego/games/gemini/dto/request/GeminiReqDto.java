package com.valuego.games.gemini.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record GeminiReqDto(
        List<Content> contents,
        GenerationConfig generationConfig
) {

    @Builder
    public record Content(
            String role,
            List<Part> parts
    ) {
    }

    @Builder
    public record Part(
            String text
    ) {
    }

    @Builder
    public record GenerationConfig(
            double temperature
    ) {
    }
}
