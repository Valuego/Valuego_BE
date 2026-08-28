package com.valuego.tourplace.service;

import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TravelScheduleMapper {

    private final TourApiService tourApiService;

    public TravelScheduleResDto toScheduleResDtoWithLiveTourApi(Travel travel) {

        // 1. DB의 Travel에서 모든 TravelPlace 추출
        List<TravelPlace> allPlaces = travel.getDays().stream()
                .flatMap(day -> day.getPlaces().stream())
                .filter(place -> place.getContentId() != null && !place.getContentId().isBlank())
                .toList();

        // 2. contentId 기반 관광공사 API 실시간 병렬 호출 (CompletableFuture)
        Map<String, TourPlace> livePlaceMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = allPlaces.stream()
                .map(place -> CompletableFuture.runAsync(() -> {
                    try {
                        TourPlace tourPlace = tourApiService.getPlaceDetail(place.getContentId());
                        if (tourPlace != null) {
                            livePlaceMap.put(place.getContentId(), tourPlace);
                        } else {
                            log.warn("관광공사 API 응답 없음 - contentId: {}", place.getContentId());
                        }
                    } catch (Exception e) {
                        log.error("관광공사 API 실시간 병렬 조회 중 에러 - contentId: {}", place.getContentId(), e);
                    }
                }))
                .toList();

        // 모든 비동기 병렬 호출이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 3. 실시간 정보(livePlaceMap)를 바인딩하여 응답 생성
        return TravelScheduleResDto.of(travel, livePlaceMap);
    }
}
