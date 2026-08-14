package com.valuego.games.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuego.games.gemini.dto.request.GeminiReqDto;
import com.valuego.games.gemini.dto.response.GeminiResDto;
import com.valuego.games.quiz.dto.response.QuizAiResDto;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.groups.entity.Enum.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiGameService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final RestTemplate template;
    private final ObjectMapper objectMapper;

    @Value("${gemini.model}")
    private String model;

    public QuizAiResDto generateLocalQuiz(Destination destination) {

        String prompt = """
                여행지 "%s"와 관련된 로컬 퀴즈를 1개 만들어줘.

                다음 조건을 반드시 지켜줘.

                1. "%s" 지역과 직접적으로 관련된 문제여야 한다.
                2. 해당 지역의 역사, 문화, 음식, 관광지 또는 지역 특색을 다양하게 활용한다.
                3. 실제 사실에 기반한 문제만 출제한다.
                4. 여행자가 재미있게 풀 수 있는 난이도로 만든다.
                5. 객관식 문제로 만든다.
                6. 보기는 정확히 4개를 만든다.
                7. 보기 번호는 A, B, C, D를 사용한다.
                8. 정답은 반드시 하나만 존재해야 한다.
                9. correctOption은 A, B, C, D 중 하나만 사용한다.
                10. explanation은 정답에 대한 간단한 설명으로 작성한다.
                11. 문제와 보기에는 불필요한 이모지를 넣지 않는다.

                반드시 아래 JSON 형식으로만 응답한다.
                Markdown이나 다른 설명은 포함하지 않는다.

                {
                  "question": "문제",
                  "options": [
                    {
                      "option": "A",
                      "content": "보기 A"
                    },
                    {
                      "option": "B",
                      "content": "보기 B"
                    },
                    {
                      "option": "C",
                      "content": "보기 C"
                    },
                    {
                      "option": "D",
                      "content": "보기 D"
                    }
                  ],
                  "correctOption": "A",
                  "explanation": "정답 설명"
                }
                """.formatted(destination, destination);

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
                                .temperature(0.8)
                                .build()
                )
                .build();

        String url = GEMINI_URL.formatted(model);

        HttpEntity<GeminiReqDto> entity =
                new HttpEntity<>(request);

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
                    ErrorCode.GAME_QUIZ_NOT_FOUND_EXCEPTION,
                    "Gemini 퀴즈 응답이 없습니다."
            );
        }

        String content = body.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();

        try {
            return objectMapper.readValue(
                    content,
                    QuizAiResDto.class
            );
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "Gemini 퀴즈 응답을 변환할 수 없습니다."
            );
        }
    }
}
