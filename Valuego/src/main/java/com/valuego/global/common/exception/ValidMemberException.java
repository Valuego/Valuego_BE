package com.valuego.global.common.exception;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidMemberException {

    private final EntityFinderException entityFinderException;
    private final GroupMemberRepository groupMemberRepository;

    // 로그인 사용자가 해당 그룹의 멤버인지 확인
    public GroupMember validateGroupMember(Principal principal, String guestToken, Group group) {
        // 로그인 사용자
        if (principal != null) {
            User user = entityFinderException.getUserFromPrincipal(principal);

            return groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                    () -> new BusinessException(ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION, "해당 그룹의 멤버가 아닙니다."));
        }

        // 게스트
        return validateGuest(group, guestToken);
    }

    // 게스트 예외 검사
    public GroupMember validateGuest(Group group, String guestToken) {
        if (guestToken == null || guestToken.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION,
                    ErrorCode.UNAUTHORIZED_EXCEPTION.getMessage());
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
