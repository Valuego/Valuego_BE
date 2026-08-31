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
import com.valuego.travel.api.dto.response.AiScheduleUpdateResDto;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelDay;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import com.valuego.travel.entity.repository.TravelRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private final TravelScheduleMapper travelScheduleMapper;
    private final TravelPlaceRepository travelPlaceRepository;

    // 1. 관광공사 api 호출
    public TourCandidatesResult fetchTourCandidates(Group group) throws Exception {
        Destination destination = group.getDestination();

        int spotLimit = 30;
        int restLimit = 30;

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

        List<TourPlace> activities = new ArrayList<>(Stream.of(
                spotFuture.get(),
                cultureFuture.get(),
                leportsFuture.get(),
                shoppingFuture.get()
        ).flatMap(List::stream).toList());

        List<TourPlace> restaurants = new ArrayList<>(restaurantFuture.get());

        if (activities.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "관광지 정보를 가져오지 못했습니다.");
        }

        Collections.shuffle(activities);
        Collections.shuffle(restaurants);

        return new TourCandidatesResult(activities, restaurants);
    }

    // 2. Gemini 최초 호출 및 DB 저장
    @Transactional
    public TravelScheduleResDto generateAndSaveSchedule(Long groupId, Group group, List<TourPlace> activities, List<TourPlace> restaurants) {
        List<GroupStyleDto> styles = groupStyleService.getGroupStyles(groupId);
        if (styles.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "여행 스타일이 설정되지 않았습니다.");
        }

        Destination destination = group.getDestination();
        int days = calculateDays(group);

        AiScheduleResDto aiResult = geminiService.generate(
                destination,
                days,
                styles,
                activities,
                restaurants
        );

        return saveScheduleToDb(groupId, group, aiResult, activities, restaurants);
    }

    @Transactional
    public TravelScheduleResDto saveSuggestedSchedule(Long groupId, Group group, AiScheduleUpdateResDto suggestedSchedule) {
        // 1. 해당 그룹의 Travel 일정 조회
        Travel travel = travelRepository.findByGroupId(groupId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION,
                        "수정할 여행 일정을 찾을 수 없습니다."
                ));

        // 2. 수정 대상 Day(TravelDay) 조회
        TravelDay targetDay = travel.getDays().stream()
                .filter(day -> day.getDayNumber().equals(suggestedSchedule.dayNumber()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.TRAVEL_DAY_NOT_FOUND_EXCEPTION,
                        suggestedSchedule.dayNumber() + "일차 일정을 찾을 수 없습니다."
                ));

        // 3. 삭제(대체)할 기존 장소 확인 및 DB 삭제
        AiScheduleUpdateResDto.OriginalPlaceDto originalPlace = suggestedSchedule.originalPlace();

        if (originalPlace != null && originalPlace.contentId() != null) {
            List<TravelPlace> placesToRemove = targetDay.getPlaces().stream()
                    .filter(place -> originalPlace.contentId().equals(place.getContentId()))
                    .toList();

            if (!placesToRemove.isEmpty()) {
                travelPlaceRepository.deleteAll(placesToRemove);
                targetDay.getPlaces().removeAll(placesToRemove);
            }
        }

        // 4. 새로운 장소(newPlaces) 객체 생성 및 리스트 추가
        List<AiScheduleUpdateResDto.NewPlaceDto> newPlaces = suggestedSchedule.newPlaces();
        if (newPlaces != null && !newPlaces.isEmpty()) {
            for (int i = 0; i < newPlaces.size(); i++) {
                AiScheduleUpdateResDto.NewPlaceDto newPlace = newPlaces.get(i);
                TourPlace tourDetail = tourApiService.getPlaceDetail(newPlace.contentId());

                LocalTime visitTime = null;
                if (newPlace.visitTime() != null && !newPlace.visitTime().isBlank()) {
                    visitTime = LocalTime.parse(newPlace.visitTime(), DateTimeFormatter.ofPattern("HH:mm"));
                }

                // scheduleOrder에 임시 값(999 등) 지정 후 저장 (NOT NULL 제약조건 우회)
                TravelPlace travelPlace = TravelPlace.builder()
                        .travelDay(targetDay)
                        .group(group)
                        .contentId(newPlace.contentId())
                        .contentTypeId(tourDetail != null ? tourDetail.getContentTypeId() : "UNKNOWN")
                        .customName(tourDetail != null ? tourDetail.getName() : null)
                        .visitTime(visitTime)
                        .scheduleOrder(999) // NOT NULL 에러 방지를 위한 기본값 세팅
                        .placeType(newPlace.placeType())
                        .reason(newPlace.reason())
                        .build();

                TravelPlace savedPlace = travelPlaceRepository.save(travelPlace);
                targetDay.getPlaces().add(savedPlace);
            }
        }

        // 5. 방문 시간(visitTime) 기준으로 일정 순서 정렬 및 scheduleOrder 올바르게 재정렬 (1부터 시작)
        List<TravelPlace> allPlaces = new ArrayList<>(targetDay.getPlaces());
        allPlaces.sort(Comparator.comparing(
                TravelPlace::getVisitTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        for (int i = 0; i < allPlaces.size(); i++) {
            allPlaces.get(i).updateScheduleOrder(i + 1);
        }

        // 6. 갱신된 전체 일정 반환
        return travelScheduleMapper.toScheduleResDtoWithLiveTourApi(travel);
    }

    // 공통 DB 저장 로직
    private TravelScheduleResDto saveScheduleToDb(Long groupId, Group group, AiScheduleResDto aiResult, List<TourPlace> activities, List<TourPlace> restaurants) {
        travelRepository.findByGroupId(groupId).ifPresent(travelRepository::delete);

        Travel travel = new Travel(group);
        Map<String, TourPlace> placeMap = new HashMap<>();
        activities.forEach(place -> placeMap.put(place.getContentId(), place));
        restaurants.forEach(place -> placeMap.put(place.getContentId(), place));

        for (AiScheduleResDto.Day aiDay : aiResult.days()) {
            TravelDay travelDay = new TravelDay(aiDay.dayNumber());
            double totalDistance = 0.0;
            TourPlace previous = null;
            int order = 1;

            for (AiScheduleResDto.Place aiPlace : aiDay.places()) {
                TourPlace place = placeMap.get(aiPlace.contentId());
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

                LocalTime visitTime = null;
                if (aiPlace.visitTime() != null && !aiPlace.visitTime().isBlank()) {
                    try {
                        visitTime = LocalTime.parse(aiPlace.visitTime());
                    } catch (DateTimeParseException e) {
                        visitTime = null;
                    }
                }

                TravelPlace travelPlace = TravelPlace.builder()
                        .travelDay(travelDay)
                        .group(group)
                        .contentId(place.getContentId())
                        .contentTypeId(place.getContentTypeId())
                        .visitTime(visitTime)
                        .scheduleOrder(order++)
                        .placeType(aiPlace.placeType())
                        .reason(aiPlace.reason())
                        .distanceFromPreviousKm(distanceKm)
                        .build();

                travelDay.addPlace(travelPlace);
                previous = place;
            }

            travelDay.updateTotalDistance(round(totalDistance));
            travel.addDay(travelDay);
        }

        Travel savedTravel = travelRepository.save(travel);
        return travelScheduleMapper.toScheduleResDtoWithLiveTourApi(savedTravel);
    }

    public int calculateDays(Group group) {
        return (int) ChronoUnit.DAYS.between(
                group.getStartDate().toLocalDate(),
                group.getEndDate().toLocalDate()
        ) + 1;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourCandidatesResult {
        private List<TourPlace> activities;
        private List<TourPlace> restaurants;
    }
}
