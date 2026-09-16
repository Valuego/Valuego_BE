package com.valuego.users.service;

import com.valuego.expense.entity.Expense;
import com.valuego.expense.entity.repository.ExpenseRepository;
import com.valuego.games.api.dto.response.GameMemberInfoResDto;
import com.valuego.games.entity.Game;
import com.valuego.games.entity.repository.GameRepository;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.tourplace.api.dto.response.TourPlace;
import com.valuego.tourplace.service.TourApiService;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import com.valuego.users.api.dto.response.UserScheduleResDto;
import com.valuego.users.api.dto.response.UserTimelineResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTimelineService {

    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final TravelPlaceRepository travelPlaceRepository;
    private final ExpenseRepository expenseRepository;
    private final GameRepository gameRepository;
    private final TourApiService tourApiService;

    // 타임라인 전체 조회 (TourAPI 실시간 연동 + 총 지출 + 게임 결과 병합)
    public UserTimelineResDto getTimeline(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        // 1. 날짜 계산
        LocalDateTime startDate = group.getStartDate();
        LocalDateTime endDate = group.getEndDate();
        LocalDate today = LocalDate.now();

        // 여행이 이미 끝난 날짜인 경우 예외 발생
        if (endDate != null && today.isAfter(endDate.toLocalDate())) {
            throw new BusinessException(ErrorCode.TRAVEL_DAY_NOT_FOUND_EXCEPTION    , "이미 종료된 여행입니다.");
        }

        int currentDay = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), today) + 1;

        // 여행 시작 전인 경우 Day 1로 고정
        if (currentDay < 1) {
            currentDay = 1;
        }

        // 2. 오늘 총 지출 금액 합산
        List<Expense> todayExpenses = expenseRepository.findAllByGroupIdAndExpenseDate(groupId, today);
        int totalExpense = todayExpenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .intValue();

        List<UserTimelineResDto.TimelineItemDto> items = new ArrayList<>();

        // 3. TravelDay.dayNumber(currentDay) 기준 오늘 일정 장소들 조회
        List<TravelPlace> places = travelPlaceRepository.findAllByGroupIdAndDayNumber(groupId, currentDay);

        // TourAPI 실시간 정보 매핑
        List<String> contentIds = places.stream()
                .map(TravelPlace::getContentId)
                .filter(Objects::nonNull)
                .toList();

        Map<String, TourPlace> tourPlaceMap = tourApiService.getPlacesDetailsMap(contentIds);

        for (TravelPlace place : places) {
            LocalTime time = place.getVisitTime() != null ? place.getVisitTime() : LocalTime.MIDNIGHT;
            LocalDateTime eventDateTime = LocalDateTime.of(today, time);

            TourPlace tourPlace = tourPlaceMap.get(place.getContentId());
            String placeName = (tourPlace != null) ? tourPlace.getName() : "장소 정보 없음";

            items.add(UserTimelineResDto.TimelineItemDto.builder()
                    .id(place.getId())
                    .title(placeName)
                    .time(eventDateTime)
                    .category(place.getPlaceType() != null ? place.getPlaceType() : "관광")
                    .description(null)
                    .build());
        }

        // 4. 오늘 진행된 게임 결과(룰렛, 사다리타기 등) 타임라인 아이템 변환
        List<Game> todayGames = gameRepository.findAllByGroupIdAndCreatedAtDate(groupId, today);

        for (Game game : todayGames) {
            String loserName = "알 수 없음";

            if (game.getResult() != null && !game.getResult().isEmpty()) {
                loserName = game.getResult().stream()
                        .filter(dto -> "당첨".equals(dto.result()) || (dto.result() != null && dto.result().contains("당첨")))
                        .map(GameMemberInfoResDto::nickname)
                        .collect(Collectors.joining(", "));

                if (loserName.isBlank()) {
                    loserName = game.getResult().get(0).nickname();
                }
            }

            String gameCategoryName = (game.getGameType() != null) ? game.getGameType().name() : "게임";
            String title = loserName + " 당첨";
            String penaltyText = (game.getPenalty() != null && !game.getPenalty().isBlank()) ? game.getPenalty() : "벌칙";
            String description = String.format("%s에서 %s님이 %s을(를) 뽑았어요", gameCategoryName, loserName, penaltyText);

            items.add(UserTimelineResDto.TimelineItemDto.builder()
                    .id(game.getId())
                    .title(title)
                    .time(game.getCreatedAt())
                    .category(gameCategoryName)
                    .description(description)
                    .build());
        }

        // 5. 장소 일정과 게임 결과를 시간(time) 순으로 종합 정렬
        items.sort(Comparator.comparing(UserTimelineResDto.TimelineItemDto::time));

        return UserTimelineResDto.builder()
                .currentDay(currentDay)
                .totalExpense(totalExpense)
                .items(items)
                .build();
    }

    // 남은 일정 조회
    public UserScheduleResDto getRemainingSchedule(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        LocalDateTime startDate = group.getStartDate();
        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        int currentDay = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), today) + 1;
        if (currentDay < 1) currentDay = 1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M.d E · a h시 m분", Locale.KOREAN);
        String currentStatus = LocalDateTime.now().format(formatter);

        LocalDate targetDate = startDate.toLocalDate().plusDays(currentDay - 1);
        BigDecimal totalExpenseBigDecimal = expenseRepository.findTotalAmountByGroupIdAndExpenseDate(groupId, targetDate);
        int totalExpense = totalExpenseBigDecimal != null ? totalExpenseBigDecimal.intValue() : 0;

        // 오늘 남은 장소 조회 (현재 시간 이후, 최대 3개)
        List<TravelPlace> top3Places = travelPlaceRepository
                .findRemainingPlacesByGroupIdAndDayNumber(groupId, currentDay, nowTime)
                .stream()
                .limit(3)
                .toList();

        List<String> contentIds = top3Places.stream()
                .map(TravelPlace::getContentId)
                .filter(Objects::nonNull)
                .toList();

        Map<String, TourPlace> tourPlaceMap = tourApiService.getPlacesDetailsMap(contentIds);

        List<UserScheduleResDto.RemainingScheduleResDto> todaySchedules = top3Places.stream()
                .map(place -> UserScheduleResDto.RemainingScheduleResDto.from(place, today, tourPlaceMap))
                .toList();

        return UserScheduleResDto.of(group, currentDay, currentStatus, totalExpense, todaySchedules);
    }
}
