package com.valuego.global.common.exception;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ValidMemberException {

    private final EntityFinderException entityFinderException;
    private final GroupMemberRepository groupMemberRepository;

    public GroupMember validateGroupMember(Principal principal, String guestTokenParam, Group group) {
        if (principal != null) {
            // 1. 카카오 회원 로그인 사용자인 경우 검증 시도
            try {
                User user = entityFinderException.getUserFromPrincipal(principal);
                Optional<GroupMember> groupMember = groupMemberRepository.findByGroupAndUser(group, user);

                if (groupMember.isPresent()) {
                    return groupMember.get();
                }
            } catch (Exception ignored) {
            }

            // 2. 로그인 유저 검증 실패 시 Principal의 값(GuestToken)으로 게스트 검증 진행
            String effectiveGuestToken = StringUtils.hasText(guestTokenParam) ? guestTokenParam : principal.getName();
            return validateGuest(group, effectiveGuestToken);
        }

        // 3. Principal이 아예 없는 경우 쿼리 파라미터/헤더의 guestToken으로 검증
        if (StringUtils.hasText(guestTokenParam)) {
            return validateGuest(group, guestTokenParam);
        }

        throw new BusinessException(ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION, "해당 그룹의 멤버가 아닙니다.");
    }

    // 게스트 예외 검사
    public GroupMember validateGuest(Group group, String guestToken) {
        if (!StringUtils.hasText(guestToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "게스트 토큰이 존재하지 않습니다.");
        }

        GroupMember groupMember = groupMemberRepository.findByGuestToken(guestToken).orElseThrow(
                () -> new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION,
                        "유효하지 않은 게스트 토큰입니다."));

        // 다른 그룹의 게스트 토큰인지 확인
        if (!groupMember.getGroup().getId().equals(group.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION,
                    "해당 그룹의 멤버가 아닙니다.");
        }

        // 게스트 토큰 만료 확인
        if (groupMember.getGuestTokenExpiresAt() != null && groupMember.getGuestTokenExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION,
                    "게스트 토큰이 만료되었습니다.");
        }

        return groupMember;
    }
}
