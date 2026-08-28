package com.valuego.styles.service;

import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.styles.entity.repository.StyleRepository;
import com.valuego.tourplace.api.dto.GroupStyleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupStyleService {

    private final GroupMemberRepository groupMemberRepository;
    private final StyleRepository styleRepository;

    // 여행 스타일 조회
    public List<GroupStyleDto> getGroupStyles(Long groupId) {

        List<GroupMember> members = groupMemberRepository.findAllByGroupId(groupId);

        return members.stream()
                .map(styleRepository::findByGroupMember)
                .flatMap(java.util.Optional::stream)
                .map(style ->
                        GroupStyleDto.builder()
                                .budgetType(
                                        style.getBudgetType().name()
                                )
                                .foodType(
                                        style.getFoodType().name()
                                )
                                .activityIntensity(
                                        style.getActivityIntensity()
                                )
                                .build()
                )
                .toList();
    }
}
