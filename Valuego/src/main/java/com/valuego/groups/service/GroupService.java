package com.valuego.groups.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.groups.api.dto.reqest.GroupCreateReqDto;
import com.valuego.groups.api.dto.response.GroupInfoResDto;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.Enum.MemberRole;
import com.valuego.groups.entity.Enum.MemberStatus;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.groups.entity.repository.GroupRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityFinderException entityFinderException;

    // 그룹 생성
    @Transactional
    public GroupInfoResDto createGroup(Principal principal, GroupCreateReqDto groupCreateReqDto) {
        User user = entityFinderException.getUserFromPrincipal(principal);

        String groupLink = UUID.randomUUID().toString();

        Group group = Group.builder()
                .title(groupCreateReqDto.title())
                .destination(groupCreateReqDto.destination())
                .startDate(groupCreateReqDto.startDate())
                .endDate(groupCreateReqDto.endDate())
                .memberCount(groupCreateReqDto.memberCount())
                .groupLink(groupLink)
                .transportType(groupCreateReqDto.transportType())
                .leader(user)
                .build();

        groupRepository.save(group);

        // 그룹을 만든 사용자를 리더로 등록
        GroupMember groupMember = GroupMember.builder()
                .memberName(user.getNickname())
                .memberColor(user.getMemberColor())
                .memberRole(MemberRole.LEADER)
                .memberStatus(MemberStatus.BEFORE_PREFERENCE)
                .user(user)
                .group(group)
                .build();

        groupMemberRepository.save(groupMember);

        // 현재 그룹 멤버 조회
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group);

        return GroupInfoResDto.from(group, groupMembers);
    }

    // 그룹 상세 조회 - 팀장만
    public GroupInfoResDto getDetailGroup(Principal principal, Long groupId) {
        User user = entityFinderException.getUserFromPrincipal(principal);
        Group group = entityFinderException.getGroupById(groupId);

        // 팀장 여부 확인
        if (!group.getLeader().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION,
                    "팀장만 그룹 상세 정보를 조회할 수 있습니다."
            );
        }

        // 그룹 멤버 조회
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group);

        return GroupInfoResDto.from(group, groupMembers);
    }
}
