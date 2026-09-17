package com.valuego.settlement.service;

import com.valuego.effort.entity.Effort;
import com.valuego.effort.entity.repository.EffortRepository;
import com.valuego.expense.entity.Expense;
import com.valuego.expense.entity.ExpensePayer;
import com.valuego.expense.entity.repository.ExpensePayerRepository;
import com.valuego.expense.entity.repository.ExpenseRepository;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.settlement.api.dto.response.SettlementResDto;
import com.valuego.settlement.api.dto.response.SettlementResDto.*;
import com.valuego.settlement.entity.Settlement;
import com.valuego.settlement.entity.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final EffortRepository effortRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpensePayerRepository expensePayerRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;

    // 통합 정산표 조회
    public SettlementResDto getIntegratedSettlement(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<GroupMember> groupMembers = group.getGroupMembers();
        int memberCount = groupMembers.size();

        // 1. 총 지출 및 1인당 기본 지출 계산 (N분의 1)
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        long totalExpense = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .mapToLong(BigDecimal::longValue)
                .sum();

        long expensePerMember = memberCount > 0 ? totalExpense / memberCount : 0L;

        // 2. 멤버별 실제 결제 총액 계산
        List<ExpensePayer> payers = expensePayerRepository.findByGroupIdWithExpense(groupId);
        Map<Long, Long> memberPaidMap = new HashMap<>();

        for (ExpensePayer payer : payers) {
            Long memberId = payer.getGroupMember().getId();
            long paid;

            if (payer.getPaidAmount() != null) {
                paid = payer.getPaidAmount().longValue();
            } else if (payer.getExpense() != null && payer.getExpense().getAmount() != null) {
                paid = payer.getExpense().getAmount().longValue();
            } else {
                paid = 0L;
            }

            memberPaidMap.put(memberId, memberPaidMap.getOrDefault(memberId, 0L) + paid);
        }

        // 3. 수고 평가 내역 및 각 멤버가 받을 보상금 계산
        List<Effort> efforts = effortRepository.findByGroupIdWithItemAndTargetMember(groupId);
        Map<Long, Long> rewardMap = new HashMap<>();
        List<EffortRewardResDto> effortRewards = new ArrayList<>();

        Map<GroupMember, List<Effort>> effortsByTarget = efforts.stream()
                .collect(Collectors.groupingBy(Effort::getTargetMember));

        for (GroupMember member : groupMembers) {
            List<Effort> targetEfforts = effortsByTarget.getOrDefault(member, Collections.emptyList());

            long rewardAmount = 0L;
            String effortTitle = member.getMemberName() + "의 수고";

            if (!targetEfforts.isEmpty()) {
                double avgAmount = targetEfforts.stream()
                        .mapToLong(Effort::getEffortAmount)
                        .average()
                        .orElse(0.0);
                rewardAmount = Math.round(avgAmount / 100.0) * 100; // 100원 단위 반올림
                effortTitle = member.getMemberName() + "의 " + targetEfforts.get(0).getEffortItem().getTitle();
            }

            rewardMap.put(member.getId(), rewardAmount);
            effortRewards.add(new EffortRewardResDto(
                    member.getId(),
                    member.getMemberName(),
                    effortTitle,
                    rewardAmount
            ));
        }

        // 4. 수고 보상 차감액 계산 (수고 보상 받는 본인 제외, 나머지 (N-1)명이 N빵)
        Map<Long, Long> burdenMap = new HashMap<>();
        for (GroupMember m : groupMembers) {
            burdenMap.put(m.getId(), 0L);
        }

        if (memberCount > 1) {
            for (GroupMember target : groupMembers) {
                long targetReward = rewardMap.getOrDefault(target.getId(), 0L);
                if (targetReward <= 0) continue;

                int remainingCount = memberCount - 1;
                long baseBurden = targetReward / remainingCount;
                long remainder = targetReward % remainingCount; // 1원 단차 오차 보정

                int index = 0;
                for (GroupMember payer : groupMembers) {
                    if (payer.getId().equals(target.getId())) continue;

                    long currentBurden = baseBurden + (index == remainingCount - 1 ? remainder : 0L);
                    burdenMap.put(payer.getId(), burdenMap.get(payer.getId()) + currentBurden);
                    index++;
                }
            }
        }

        // 5. 최종 정산 내역 계산: (실제 결제액 - 1인당 기본 지출) + (받을 보상금 - 내가 부담할 보상금)
        List<MemberSettlementResDto> memberSettlements = new ArrayList<>();

        for (GroupMember member : groupMembers) {
            long actualPaid = memberPaidMap.getOrDefault(member.getId(), 0L);
            long myReward = rewardMap.getOrDefault(member.getId(), 0L);
            long myBurden = burdenMap.getOrDefault(member.getId(), 0L);

            long netAmount = (actualPaid - expensePerMember) + (myReward - myBurden);

            SettlementType type;
            long finalAmount = Math.abs(netAmount);

            if (netAmount > 0) {
                type = SettlementType.GIVE; // 받아요 (+)
            } else if (netAmount < 0) {
                type = SettlementType.SEND; // 보내요 (-)
            } else {
                type = SettlementType.ZERO;
            }

            memberSettlements.add(new MemberSettlementResDto(
                    member.getId(),
                    member.getMemberName(),
                    type,
                    finalAmount
            ));
        }

        // 6. 정산표 확인 완료 여부 조회
        Boolean isConfirmed = settlementRepository.findByGroupId(groupId)
                .map(Settlement::getIsConfirmed)
                .orElse(false);

        return new SettlementResDto(
                totalExpense,
                expensePerMember,
                isConfirmed,
                effortRewards,
                memberSettlements
        );
    }

    @Transactional
    public void confirmSettlement(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        GroupMember currentMember = validMemberException.validateGroupMember(principal, guestToken, group);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        long totalExpense = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .mapToLong(BigDecimal::longValue)
                .sum();

        int memberCount = group.getGroupMembers().size();
        long expensePerMember = memberCount > 0 ? totalExpense / memberCount : 0L;

        Settlement settlement = settlementRepository.findByGroupId(groupId)
                .orElseGet(() -> Settlement.builder()
                        .group(group)
                        .totalExpense(totalExpense)
                        .expensePerMember(expensePerMember)
                        .build());

        // 해당 멤버의 확인 도장 등록 (모든 멤버 수 달성 시 isConfirmed = true 변환)
        settlement.confirmMember(currentMember.getId(), memberCount, totalExpense, expensePerMember);
        settlementRepository.save(settlement);
    }
}
