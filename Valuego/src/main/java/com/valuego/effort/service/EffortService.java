package com.valuego.effort.service;

import com.valuego.effort.api.dto.request.EffortReqDto;
import com.valuego.effort.api.dto.response.EffortInfoResDto;
import com.valuego.effort.api.dto.response.EffortResultResDto;
import com.valuego.effort.entity.Effort;
import com.valuego.effort.entity.EffortItem;
import com.valuego.effort.entity.repository.EffortItemRepository;
import com.valuego.effort.entity.repository.EffortRepository;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EffortService {

    private final EffortRepository effortRepository;
    private final EffortItemRepository effortItemRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;

    // 수고 회고 생성
    @Transactional
    public EffortInfoResDto createEffort(Principal principal, EffortReqDto effortReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(effortReqDto.groupId());

        GroupMember writerMember = validMemberException.validateGroupMember(principal, guestToken, group);
        GroupMember targetMember = entityFinderException.getGroupMemberById(effortReqDto.targetMemberId());

        EffortItem effortItem = entityFinderException.getEffortItemById(effortReqDto.effortItemId());

        Effort effort = Effort.builder()
                .group(group)
                .writerMember(writerMember)
                .targetMember(targetMember)
                .effortItem(effortItem)
                .effortAmount(effortReqDto.effortAmount())
                .comment(effortReqDto.comment())
                .build();

        Effort savedEffort = effortRepository.save(effort);
        return EffortInfoResDto.from(savedEffort);
    }

    // 결과 조회
    public EffortResultResDto getEffortResult(Principal principal, Long groupId, Long targetMemberId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        GroupMember targetMember = entityFinderException.getGroupMemberById(targetMemberId);
        List<Effort> efforts = effortRepository.findByGroupIdAndTargetMemberId(groupId, targetMemberId);

        List<Long> amounts = efforts.stream()
                .map(Effort::getEffortAmount)
                .filter(amount -> amount != null)
                .toList();

        Long averageAmount = calculateAverage(amounts);

        List<String> comments = efforts.stream()
                .map(Effort::getComment)
                .filter(c -> c != null && !c.isBlank())
                .toList();

        return EffortResultResDto.of(
                targetMember.getId(),
                targetMember.getMemberName(),
                averageAmount,
                efforts.size(),
                comments
        );
    }

    // 평균값 계산
    private Long calculateAverage(List<Long> amounts) {
        if (amounts.isEmpty()) {
            return 0L;
        }

        double average = amounts.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        return Math.round(average / 100.0) * 100;
    }
}
