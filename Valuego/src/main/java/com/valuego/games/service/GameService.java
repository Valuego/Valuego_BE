package com.valuego.games.service;

import com.valuego.games.api.dto.request.GameCreateReqDto;
import com.valuego.games.api.dto.response.GameMemberListResDto;
import com.valuego.games.api.dto.response.GameMemberInfoResDto;
import com.valuego.games.api.dto.response.GameResultResDto;
import com.valuego.games.entity.Game;
import com.valuego.games.entity.GameType;
import com.valuego.games.entity.repository.GameRepository;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;

    // 사다리 타기
    public GameResultResDto createLadder(Principal principal, Long groupId, GameCreateReqDto gameCreateReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<GroupMember> members = groupMemberRepository.findAllByGroupId(groupId);
        validateMembers(members);

        List<String> results = createLadderResults(members.size());

        List<GameMemberInfoResDto> gameMemberInfoResDtos = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            gameMemberInfoResDtos.add(
                    GameMemberInfoResDto.from(
                            members.get(i),
                            results.get(i)
                    )
            );
        }

        Game game = Game.builder()
                .group(group)
                .gameType(GameType.LADDER)
                .penalty(gameCreateReqDto.penalty())
                .result(gameMemberInfoResDtos)
                .build();

        gameRepository.save(game);

        return GameResultResDto.from(game);
    }

    // 룰렛
    public GameResultResDto createRoulette(Principal principal, Long groupId, GameCreateReqDto gameCreateReqDto, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        List<GroupMember> members = groupMemberRepository.findAllByGroupId(groupId);
        validateMembers(members);

        GroupMember selectedMember = selectRandomMember(members);

        List<GameMemberInfoResDto> gameMemberInfoResDto =
                List.of(GameMemberInfoResDto.from(
                        selectedMember,
                        "당첨"
                ));

        Game game = Game.builder()
                .group(group)
                .gameType(GameType.ROULETTE)
                .penalty(gameCreateReqDto.penalty())
                .result(gameMemberInfoResDto)
                .build();

        gameRepository.save(game);

        return GameResultResDto.from(game);
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

    // 룰렛 결과 생성
    private GroupMember selectRandomMember(List<GroupMember> members) {
        return members.get(ThreadLocalRandom.current().nextInt(members.size()));
    }

    // 그룹별 게임 멤버 리스트 조회
    public List<GameMemberListResDto> getGameMembers(Principal principal, Long groupId, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        validMemberException.validateGroupMember(principal, guestToken, group);

        return groupMemberRepository
                .findAllByGroupIdOrderByIdAsc(groupId)
                .stream()
                .map(GameMemberListResDto::from)
                .toList();
    }
}
