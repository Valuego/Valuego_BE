package com.valuego.groups.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.groups.api.dto.reqest.GroupCreateReqDto;
import com.valuego.groups.api.dto.response.GroupInfoResDto;
import com.valuego.groups.api.dto.response.GroupListResDto;
import com.valuego.groups.entity.Enum.GroupStatus;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .groupStatus(GroupStatus.PLANNING) // 초기 상태(계획 중)
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

    // 전체 여행 리스트 조회(카카오 로그인 사용자)
    public GroupListResDto getMyGroups(Principal principal) {
        User user = entityFinderException.getUserFromPrincipal(principal);

        // 내가 팀장인 그룹 전체 조회
        List<Group> groups = groupRepository.findAllByLeaderOrderByStartDateDesc(user);

        if (groups.isEmpty()) {
            return new GroupListResDto(List.of(), List.of());
        }

        // 모든 그룹의 멤버를 한 번에 조회
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupIn(groups);

        // groupId별 멤버 그룹화
        Map<Long, List<GroupMember>> membersByGroupId = groupMembers.stream()
                        .collect(Collectors.groupingBy(member -> member.getGroup().getId()));

        LocalDateTime now = LocalDateTime.now();

        // 진행 중인 여행 - 종료 날짜 dday 포함
        List<GroupInfoResDto> ongoingGroups = groups.stream()
                        .filter(group -> !now.isAfter(group.getEndDate()))
                        .map(group -> GroupInfoResDto.from(group, membersByGroupId.getOrDefault(group.getId(), List.of())))
                        .toList();

        // 지난 여행
        List<GroupInfoResDto> pastGroups = groups.stream()
                        .filter(group -> now.isAfter(group.getEndDate()))
                        .map(group -> GroupInfoResDto.from(group, membersByGroupId.getOrDefault(group.getId(), List.of())))
                        .toList();

        return new GroupListResDto(ongoingGroups, pastGroups);
    }

    // ai 일정 생성 위한 그룹 조회
    public Group getGroup(Principal principal, Long groupId) {
        User user = entityFinderException.getUserFromPrincipal(principal);
        Group group = entityFinderException.getGroupById(groupId);

        if (!group.getLeader().getId().equals(user.getId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "팀장만 여행 일정을 생성할 수 있습니다."
            );
        }

        return group;
    }
}
