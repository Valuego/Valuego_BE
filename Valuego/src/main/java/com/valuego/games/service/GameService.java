package com.valuego.games.service;

import com.valuego.games.api.dto.request.GameCreateReqDto;
import com.valuego.games.api.dto.response.GameMemberResDto;
import com.valuego.games.api.dto.response.GameResDto;
import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import com.valuego.games.entity.repository.GameRepository;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityFinderException entityFinderException;

    // 사다리 타기
    public GameResDto createLadder(Principal principal, Long groupId, GameCreateReqDto gameCreateReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validateGroupMember(principal, guestToken, group);

        List<GroupMember> members = groupMemberRepository.findAllByGroupId(groupId);
        validateMembers(members);

        List<String> results = createLadderResults(members.size());

        List<GameMemberResDto> gameMemberResDtos = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            gameMemberResDtos.add(
                    GameMemberResDto.from(
                            members.get(i),
                            results.get(i)
                    )
            );
        }

        Game game = Game.builder()
                .group(group)
                .gameType(GameType.LADDER)
                .penalty(gameCreateReqDto.penalty())
                .result(gameMemberResDtos)
                .build();

        gameRepository.save(game);

        return GameResDto.from(game);
    }

    // 공통 메소드
    private void validateMembers(List<GroupMember> members) {
        if (members.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION,
                    ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage()
            );
        }
    }

    // 로그인 사용자가 해당 그룹의 멤버인지 확인
    private GroupMember validateGroupMember(Principal principal, String guestToken, Group group) {
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
    private GroupMember validateGuest(Group group, String guestToken) {
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

    // 사다리 결과 생성
    private List<String> createLadderResults(int memberCount) {
        List<String> results = new ArrayList<>();

        results.add("당첨");

        for (int i = 1; i < memberCount; i++) {
            results.add("통과");
        }

        Collections.shuffle(results);

        return results;
    }
}
