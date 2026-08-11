package com.valuego.groups.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.api.dto.reqest.GroupGuestInfoReqDto;
import com.valuego.groups.api.dto.response.GroupGuestJoinResDto;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.Enum.MemberRole;
import com.valuego.groups.entity.Enum.MemberStatus;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final EntityFinderException entityFinderException;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.sameSite}")
    private String cookieSameSite;

    // 게스트 그룹 참여
    @Transactional
    public ResponseEntity<ApiResTemplate<GroupGuestJoinResDto>> inviteGroup(String groupLink, GroupGuestInfoReqDto request) {
        Group group = entityFinderException.getGroupByGroupLink(groupLink);

        // 여행 종료 여부 확인
        if (LocalDateTime.now().isAfter(group.getEndDate())) {
            throw new BusinessException(
                    ErrorCode.ALREADY_FINISHED_GROUP_EXCEPTION,
                    ErrorCode.ALREADY_FINISHED_GROUP_EXCEPTION.getMessage()
            );
        }

        // 현재 참여 인원 확인
        long currentMemberCount = groupMemberRepository.countByGroup(group);

        // 그룹 정원 확인
        if (currentMemberCount >= group.getMemberCount()) {
            throw new BusinessException(
                    ErrorCode.NO_REMAINING_MEMBER_COUNT,
                    ErrorCode.NO_REMAINING_MEMBER_COUNT.getMessage()
            );
        }

        // 게스트 인증 토큰 생성
        String guestAccessToken = UUID.randomUUID().toString();

        GroupMember groupMember = GroupMember.builder()
                .memberName(request.memberName())
                .memberColor(request.memberColor())
                .memberRole(MemberRole.MEMBER)
                .memberStatus(MemberStatus.BEFORE_PREFERENCE)
                .guestToken(guestAccessToken)
                .guestTokenExpiresAt(group.getEndDate())
                .group(group)
                .build();

        groupMemberRepository.save(groupMember);

        GroupGuestJoinResDto groupGuestJoinResDto = GroupGuestJoinResDto.from(groupMember);

        // 게스트 토큰 유효 시간: 여행 종료일
        ResponseCookie guestCookie = createGuestCookie(guestAccessToken, group.getEndDate());

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.SET_COOKIE,
                guestCookie.toString()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResTemplate.successResponse(
                        SuccessCode.CREATE_SUCCESS,
                        groupGuestJoinResDto)
                );
    }

    // 게스트 인증 쿠키 생성
    private ResponseCookie createGuestCookie(String guestAccessToken, LocalDateTime expiresAt) {
        Duration duration = Duration.between(LocalDateTime.now(), expiresAt);

        return ResponseCookie.from(
                        "guestAccessToken",
                        guestAccessToken
                )
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(duration)
                .sameSite(cookieSameSite)
                .build();
    }

    // 게스트 인증 토큰으로 멤버 조회
    public GroupMember getGuestMember(
            String guestToken
    ) {
        GroupMember groupMember = entityFinderException.getGroupMemberByGuestToken(guestToken);

        // 토큰 만료 확인
        if (groupMember.getGuestTokenExpiresAt() == null || LocalDateTime.now().isAfter(groupMember.getGuestTokenExpiresAt())) {
            throw new BusinessException(
                    ErrorCode.JWT_EXPIRED,
                    ErrorCode.JWT_EXPIRED.getMessage()
            );
        }

        return groupMember;
    }
}
