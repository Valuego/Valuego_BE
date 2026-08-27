package com.valuego.tourplace.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.groups.entity.Enum.Destination;
import com.valuego.groups.entity.Group;
import com.valuego.styles.service.GroupStyleService;
import com.valuego.tourplace.api.dto.GroupStyleDto;
import com.valuego.tourplace.api.dto.response.AiScheduleResDto;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelDay;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AiScheduleService {

    private final GroupStyleService groupStyleService;
    private final TourApiService tourApiService;
    private final TravelRepository travelRepository;
    private final GeminiService geminiService;
    private final DistanceService distanceService;

    // 1. 관광공사 api 호출
    public TourCandidatesResult fetchTourCandidates(Group group) throws Exception {
        Destination destination = group.getDestination();

        int spotLimit = 10;
        int restLimit = 15;

        CompletableFuture<List<TourPlace>> spotFuture =
                CompletableFuture.supplyAsync(() -> tourApiService.getPlaces(destination, 12, spotLimit));
        CompletableFuture<List<TourPlace>> cultureFuture =
                CompletableFuture.supplyAsync(() -> tourApiService.getPlaces(destination, 14, spotLimit));
        CompletableFuture<List<TourPlace>> leportsFuture =
                CompletableFuture.supplyAsync(() -> tourApiService.getPlaces(destination, 28, spotLimit));
        CompletableFuture<List<TourPlace>> shoppingFuture =
                CompletableFuture.supplyAsync(() -> tourApiService.getPlaces(destination, 38, spotLimit));
        CompletableFuture<List<TourPlace>> restaurantFuture =
                CompletableFuture.supplyAsync(() -> tourApiService.getPlaces(destination, 39, restLimit));

        CompletableFuture.allOf(spotFuture, cultureFuture, leportsFuture, shoppingFuture, restaurantFuture).join();

        List<TourPlace> activities = Stream.of(
                spotFuture.get(),
                cultureFuture.get(),
                leportsFuture.get(),
                shoppingFuture.get()
        ).flatMap(List::stream).toList();

        List<TourPlace> restaurants = restaurantFuture.get();

        if (activities.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "관광지 정보를 가져오지 못했습니다.");
        }

        return new TourCandidatesResult(activities, restaurants);
    }

    // 2. gemini 호출 및 일정 저장
    @Transactional
    public TravelScheduleResDto generateAndSaveSchedule(Long groupId, Group group, List<TourPlace> activities, List<TourPlace> restaurants) {
        // 1. 그룹 여행 스타일 조회
        List<GroupStyleDto> styles = groupStyleService.getGroupStyles(groupId);
        if (styles.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "여행 스타일이 설정되지 않았습니다.");
        }

        // 2. 여행지 및 기간 계산
        Destination destination = group.getDestination();
        int days = (int) ChronoUnit.DAYS.between(
                group.getStartDate().toLocalDate(),
                group.getEndDate().toLocalDate()
        ) + 1;

        // 3. gemini 압축 프롬프트 및 JSON 호출
        AiScheduleResDto aiResult = geminiService.generate(
                destination,
                days,
                styles,
                activities,
                restaurants
        );

        // 4. 기존 일정 삭제
        travelRepository.findByGroupId(groupId).ifPresent(travelRepository::delete);

        // 5. Schedule 생성
        Travel travel = new Travel(group);
        Map<String, TourPlace> placeMap = new HashMap<>();
        activities.forEach(place -> placeMap.put(place.getContentId(), place));
        restaurants.forEach(place -> placeMap.put(place.getContentId(), place));

        // 6. entity 매핑 및 거리 계산
        for (AiScheduleResDto.Day aiDay : aiResult.getDays()) {
            TravelDay travelDay = new TravelDay(aiDay.getDayNumber());
            double totalDistance = 0.0;
            TourPlace previous = null;
            int order = 1;

            for (AiScheduleResDto.Place aiPlace : aiDay.getPlaces()) {
                TourPlace place = placeMap.get(aiPlace.getContentId());
                if (place == null) continue;

                Double distanceKm = null;
                if (previous != null) {
                    double distance = distanceService.calculate(
                            previous.getLatitude(),
                            previous.getLongitude(),
                            place.getLatitude(),
                            place.getLongitude()
                    );
                    distanceKm = round(distance);
                    totalDistance += distance;
                }

                TravelPlace travelPlace = TravelPlace.builder()
                        .contentId(place.getContentId())
                        .placeName(place.getName())
                        .address(place.getAddress())
                        .imageUrl(place.getImageUrl())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .scheduleOrder(order++)
                        .placeType(aiPlace.getPlaceType())
                        .reason(aiPlace.getReason())
                        .distanceFromPreviousKm(distanceKm)
                        .build();

                travelDay.addPlace(travelPlace);
                previous = place;
            }

            travelDay.updateTotalDistance(round(totalDistance));
            travel.addDay(travelDay);
        }

        Travel savedTravel = travelRepository.save(travel);
        return TravelScheduleResDto.from(savedTravel);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    // 내부 dto
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourCandidatesResult {
        private List<TourPlace> activities;
        private List<TourPlace> restaurants;
    }
}
