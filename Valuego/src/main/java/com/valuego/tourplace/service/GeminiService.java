package com.valuego.tourplace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuego.games.gemini.dto.response.GeminiResDto;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.groups.entity.Enum.Destination;
import com.valuego.tourplace.api.dto.GeminiCandidatePlaceDto;
import com.valuego.tourplace.api.dto.GroupStyleDto;
import com.valuego.tourplace.api.dto.response.AiScheduleResDto;
import com.valuego.tourplace.api.dto.response.TourPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final RestTemplate template;
    private final ObjectMapper objectMapper;

    @Value("${gemini.model}")
    private String model;

    public AiScheduleResDto generate(Destination destination, int days, List<GroupStyleDto> styles, List<TourPlace> activities, List<TourPlace> restaurants) {
        // 1. 프롬프트 토큰 다이어트: 필요한 필드(id, name, type)만 추출
        List<GeminiCandidatePlaceDto> candidateActivities = activities.stream()
                .map(place -> new GeminiCandidatePlaceDto(place, place.getLatitude(), place.getLongitude()))
                .toList();

        List<GeminiCandidatePlaceDto> candidateRestaurants = restaurants.stream()
                .map(place -> new GeminiCandidatePlaceDto(place, place.getLatitude(), place.getLongitude()))
                .toList();

        String activitiesJson;
        String restaurantsJson;
        String stylesJson;

        try {
            activitiesJson = objectMapper.writeValueAsString(candidateActivities);
            restaurantsJson = objectMapper.writeValueAsString(candidateRestaurants);
            stylesJson = objectMapper.writeValueAsString(styles);
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "후보 장소 데이터를 JSON으로 변환할 수 없습니다."
            );
        }

        // 2. 프롬프트 수정
        String prompt = String.format("""
                당신은 전문 여행 일정 설계 플래너입니다. 제공된 후보 장소 목록을 바탕으로 최적의 여행 일정을 생성해야 합니다. 아래의 규칙을 절대적으로 준수하세요.
                
                [여행 목적지]
                %s
                
                [여행 그룹 성향]
                %s
                
                [활동/관광지 후보 리스트 (이 목록에 있는 contentId만 사용하세요)]
                %s
                
                [음식점 후보 리스트 (이 목록에 있는 contentId만 사용하세요)]
                %s
                
                [요구사항]
                1. 반드시 위에 제공된 후보 장소들의 'contentId' 값 중 **존재하는 정확한 값**만 골라서 사용하세요. 임의로 숫자를 지어내거나(예: "1"), 성향 정보를 contentId에 넣는 행위를 절대 금지합니다.
                2. 하루당 오전/오후 활동 2~3곳, 점심/저녁 식당 2곳 정도로 적절히 배치하세요. 
                3. 하루당 각 활동의 위치를 고려하여 최적의 동선으로 구성하세요.
                4. 모든 후보 장소의 latitude(위도)와 longitude(경도)를 분석하여 연속된 장소 간의 이동 거리가 최소화되도록 배치하세요.
                5. 반드시 아래 JSON 형식으로만 응답하세요. 마크다운(```json) 없이 순수 JSON만 반환해야 합니다.
                6. 제공된 후보 장소 리스트를 광범위하게 활용하여, 매번 동일한 유명 명소만 반복해서 뽑지 말고 다양한 테마와 숨은 명소를 포함한 독창적인 동선을 구성해 주세요.
                
                [응답 JSON 스키마]
                {
                  "days": [
                    {
                      "dayNumber": 1,
                      "places": [
                        {
                          "contentId": "후보 장소 리스트에 있는 실제 contentId 문자열",
                          "visitTime": "HH:mm 형식의 방문 예정 시간 (예: 10:30, 12:30)",
                          "placeType": "TOUR / RESTAURANT",
                          "reason": "해당 장소를 추천한 간단한 이유 (1문장)"
                        }
                      ]
                    }
                  ]
                }
                """, destination.name(), stylesJson, activitiesJson, restaurantsJson);

        // 3. Gemini Request Map 구성
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.8
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. API 호출
        String url = GEMINI_URL.formatted(model);

        ResponseEntity<GeminiResDto> response = template.exchange(
                url,
                HttpMethod.POST,
                entity,
                GeminiResDto.class
        );

        GeminiResDto body = response.getBody();

        // 5. 응답 유효성 검증
        if (body == null
                || body.getCandidates() == null
                || body.getCandidates().isEmpty()
                || body.getCandidates().get(0).getContent() == null
                || body.getCandidates().get(0).getContent().getParts() == null
                || body.getCandidates().get(0).getContent().getParts().isEmpty()) {

            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "Gemini 일정 생성 응답이 없습니다."
            );
        }

        String content = body.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
        log.info("Gemini Raw Response: {}", content);

        // 6. DTO 매핑
        try {
            return objectMapper.readValue(content, AiScheduleResDto.class);
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "Gemini 일정 응답을 변환할 수 없습니다."
            );
        }
    }
}
