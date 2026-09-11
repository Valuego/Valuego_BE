package com.valuego.effort.service;

import com.valuego.effort.api.dto.request.EffortItemReqDto;
import com.valuego.effort.api.dto.response.EffortItemResDto;
import com.valuego.effort.entity.EffortItem;
import com.valuego.effort.entity.repository.EffortItemRepository;
import com.valuego.effort.entity.repository.EffortRepository;
import com.valuego.games.api.dto.response.GameMemberListResDto;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.code.ErrorCode;
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
public class EffortItemService {

    private final EffortItemRepository effortItemRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final EffortRepository effortRepository;

    // 직접 추가
    @Transactional
    public EffortItemResDto createEffortItem(Principal principal, Long groupId, EffortItemReqDto effortItemReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        String itemTitle;

        // 선택한 카테고리에 맞춰 항목 이름 결정
        switch (effortItemReqDto.itemCategory()) {
            case DRIVING -> itemTitle = "운전";
            case RESERVATION -> itemTitle = "예약";
            case ETC -> {
                if (effortItemReqDto.title() == null || effortItemReqDto.title().isBlank()) {
                    throw new BusinessException(ErrorCode.VALIDATION_ERROR, "기타 수고 항목 이름을 입력해 주세요.");
                }
                itemTitle = effortItemReqDto.title().trim();
            }
            default -> throw new BusinessException(ErrorCode.VALIDATION_ERROR, "올바르지 않은 수고 카테고리입니다.");
        }

        EffortItem effortItem = EffortItem.createCustomItem(group, itemTitle);
        EffortItem saved = effortItemRepository.save(effortItem);

        List<GameMemberListResDto> memberList = group.getGroupMembers().stream()
                .map(GameMemberListResDto::from)
                .toList();

        return EffortItemResDto.of(saved, memberList);
    }

    // 삭제
    @Transactional
    public void deleteEffortItem(Principal principal, Long groupId, Long effortItemId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);
        EffortItem effortItem = entityFinderException.getEffortItemById(effortItemId);

        // 커스텀 항목만 삭제 가능하도록 검증
        if (Boolean.FALSE.equals(effortItem.getIsCustom())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "기본 수고 항목은 삭제할 수 없습니다.");
        }

        effortItemRepository.delete(effortItem);
    }

    // 전체 조회
    public List<EffortItemResDto> getEffortItems(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<EffortItem> items = effortItemRepository.findDefaultAndCustomByGroupId(groupId);

        List<GroupMember> groupMembers = group.getGroupMembers();

        List<GameMemberListResDto> memberList = groupMembers.stream()
                .map(GameMemberListResDto::from)
                .toList();

        return items.stream()
                .map(item -> EffortItemResDto.of(item, memberList))
                .toList();
    }
}
