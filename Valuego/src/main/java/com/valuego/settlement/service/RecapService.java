package com.valuego.settlement.service;

import com.valuego.effort.entity.Effort;
import com.valuego.effort.entity.repository.EffortRepository;
import com.valuego.expense.entity.Expense;
import com.valuego.expense.entity.repository.ExpenseRepository;
import com.valuego.games.entity.Game;
import com.valuego.games.entity.repository.GameRepository;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.settlement.api.dto.response.SettlementRecapResDto;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecapService {

    private final EffortRepository effortRepository;
    private final ExpenseRepository expenseRepository;
    private final GameRepository gameRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final TravelPlaceRepository travelPlaceRepository;

    // 리캡 정보 조회
    public SettlementRecapResDto getSettlementRecap(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        // 1. 총 지출 금액 계산
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        BigDecimal totalExpenseAmount = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. 인정받은 총 수고 가치 계산
        List<Effort> efforts = effortRepository.findByGroupIdWithItemAndTargetMember(groupId);
        Map<GroupMember, List<Effort>> effortsByTarget = efforts.stream()
                .collect(Collectors.groupingBy(Effort::getTargetMember));

        long totalEffortSum = 0L;
        for (List<Effort> targetEfforts : effortsByTarget.values()) {
            double avgAmount = targetEfforts.stream()
                    .mapToLong(Effort::getEffortAmount)
                    .average()
                    .orElse(0.0);
            totalEffortSum += Math.round(avgAmount / 100.0) * 100;
        }
        BigDecimal totalEffortAmount = BigDecimal.valueOf(totalEffortSum);

        // 3. 게임 요약
        List<Game> games = gameRepository.findByGroupId(groupId);
        String miniGameSummary;
        if (games.isEmpty()) {
            miniGameSummary = "진행한 게임 없음";
        } else {
            String gameTypesStr = games.stream()
                    .map(game -> game.getGameType().name())
                    .distinct()
                    .limit(3)
                    .collect(Collectors.joining("·"));
            miniGameSummary = String.format("%s %d판", gameTypesStr, games.size());
        }

        // 4. 여행 기간 및 N박 M일 계산
        String travelPeriod = formatTravelPeriod(group.getStartDate(), group.getEndDate());
        String durationText = calculateDurationText(group.getStartDate(), group.getEndDate());

        // 5. DB에서 실제 이동거리(distance_from_previous_km) 합산 조회
        BigDecimal totalDistanceKm = travelPlaceRepository.sumDistanceFromPreviousKmByGroupId(groupId);
        String totalDistance;

        if (totalDistanceKm != null && totalDistanceKm.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal roundedDistance = totalDistanceKm.setScale(1, RoundingMode.HALF_UP);
            totalDistance = roundedDistance.stripTrailingZeros().toPlainString() + "km";
        } else {
            totalDistance = "0km";
        }

        return SettlementRecapResDto.of(
                groupId,
                group.getTitle(),
                travelPeriod,
                durationText,
                group.getGroupMembers().size(),
                totalDistance,
                totalExpenseAmount,
                miniGameSummary,
                totalEffortAmount
        );
    }

    private String formatTravelPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return String.format("%s - %s", startDate.format(formatter), endDate.format(formatter));
    }

    private String calculateDurationText(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return "당일치기";
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long nights = days - 1;
        return nights <= 0 ? "당일치기" : String.format("%d박 %d일", nights, days);
    }
}
