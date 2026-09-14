package com.valuego.styles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuego.games.gemini.dto.request.GeminiReqDto;
import com.valuego.games.gemini.dto.response.GeminiResDto;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.styles.api.dto.response.StyleAiResDto;
import com.valuego.styles.entity.Style;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StyleGeminiService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final RestTemplate template;
    private final ObjectMapper objectMapper;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    public StyleAiResDto analyzeLeaderStyle(Style style) {
        String activityText = getActivityIntensityText(style.getActivityIntensity());
        String budgetText = style.getBudgetType() != null ? style.getBudgetType().name() : "MEDIUM";
        String foodText = style.getFoodType() != null ? style.getFoodType().name() : "KOREAN";

        String prompt = """
                여행자의 성향 정보를 바탕으로 위트 있고 매력적인 "나의 여행 DNA" 분석 결과를 만들어줘.

                [여행자 성향 정보]
                - 활동 강도: %s (1: 여유로움 ~ 5: 매우 알참)
                - 예산 감각: %s (SAVING: 알뜰, MEDIUM: 적당히, LUXURY: 플렉스)
                - 선호 음식: %s

                다음 조건을 반드시 지켜줘.
                1. dnaTitle: 성향을 한눈에 보여주는 8자 이내의 창의적인 헤드라인 타이틀 (ex: "액티비티 러버", "힐링 탐험가", "가성비 식객")
                2. dnaDescription: 해당 여행 성향의 특징을 잘 나타내는 1줄 설명 (ex: "알찬 일정을 좋아하고, 맛집은 꼭 들르는 타입")
                3. tags: 여행 성향을 나타내는 '#' 포함 태그 3개 배열 (ex: ["#부지런", "#맛집헌터", "#가성비"])
                4. 불필요한 이모지나 마크다운 기호 없이 순수 text만 사용한다.

                반드시 아래 JSON 형식으로만 응답한다.
                Markdown이나 다른 설명은 포함하지 않는다.

                {
                  "dnaTitle": "액티비티 러버",
                  "dnaDescription": "알찬 일정을 좋아하고, 맛집은 꼭 들르는 타입",
                  "tags": [
                    "#부지런",
                    "#맛집헌터",
                    "#가성비"
                  ]
                }
                """.formatted(activityText, budgetText, foodText);

        GeminiReqDto request = GeminiReqDto.builder()
                .contents(List.of(
                        GeminiReqDto.Content.builder()
                                .role("user")
                                .parts(List.of(
                                        GeminiReqDto.Part.builder()
                                                .text(prompt)
                                                .build()
                                ))
                                .build()
                ))
                .generationConfig(
                        GeminiReqDto.GenerationConfig.builder()
                                .temperature(0.7)
                                .build()
                )
                .build();

        String url = GEMINI_URL.formatted(model);
        HttpEntity<GeminiReqDto> entity = new HttpEntity<>(request);

        ResponseEntity<GeminiResDto> response = template.exchange(
                url,
                HttpMethod.POST,
                entity,
                GeminiResDto.class
        );

        GeminiResDto body = response.getBody();

        if (body == null
                || body.getCandidates() == null
                || body.getCandidates().isEmpty()
                || body.getCandidates().get(0).getContent() == null
                || body.getCandidates().get(0).getContent().getParts() == null
                || body.getCandidates().get(0).getContent().getParts().isEmpty()) {

            throw new BusinessException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Gemini 성향 분석 응답이 없습니다."
            );
        }

        String rawContent = body.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();

        // Markdown ```json 코드블록이 포함되어 돌아올 경우 대비 정제
        String cleanJson = rawContent.replaceAll("```json", "").replaceAll("```", "").trim();

        try {
            return objectMapper.readValue(cleanJson, StyleAiResDto.class);
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Gemini 성향 분석 응답을 변환할 수 없습니다."
            );
        }
    }

    private String getActivityIntensityText(Integer intensity) {
        if (intensity == null) return "3 (적당함)";
        return intensity + " (" + (intensity >= 4 ? "알차게" : (intensity <= 2 ? "여유롭게" : "적당히")) + ")";
    }
}
