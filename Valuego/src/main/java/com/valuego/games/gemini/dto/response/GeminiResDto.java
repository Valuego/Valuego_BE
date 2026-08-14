package com.valuego.games.gemini.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class GeminiResDto {

    private List<Candidate> candidates;

    @Getter
    public static class Candidate {
        private Content content;
    }

    @Getter
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    public static class Part {
        private String text;
    }
}
