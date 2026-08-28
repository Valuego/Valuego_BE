package com.valuego.global.common.exception;

import com.valuego.games.entity.Game;
import com.valuego.games.entity.repository.GameRepository;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.groups.entity.repository.GroupRepository;
import com.valuego.travel.entity.Travel;
import com.valuego.travel.entity.TravelPlace;
import com.valuego.travel.entity.repository.TravelPlaceRepository;
import com.valuego.travel.entity.repository.TravelRepository;
import com.valuego.users.entity.User;
import com.valuego.users.entity.UserNotificationAgree;
import com.valuego.users.entity.repository.UserNotificationAgreeRepository;
import com.valuego.users.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class EntityFinderException {

    private final UserRepository userRepository;
    private final UserNotificationAgreeRepository userNotificationAgreeRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GameRepository gameRepository;
    private final TravelRepository travelRepository;
    private final TravelPlaceRepository travelPlaceRepository;

    public User getUserFromPrincipal(Principal principal) {
        Long id = Long.parseLong(principal.getName());
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + id));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }

    public UserNotificationAgree getUserNotificationAgree(User user) {
        return userNotificationAgreeRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTIFICATION_AGREE_NOT_FOUND
                        , ErrorCode.USER_NOTIFICATION_AGREE_NOT_FOUND.getMessage() + user));
    }

    // Group 조회 - ID
    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND_EXCEPTION
                        , ErrorCode.GROUP_NOT_FOUND_EXCEPTION.getMessage() + groupId));
    }

    // Group 조회 - 초대 링크
    public Group getGroupByGroupLink(String groupLink) {
        return groupRepository.findByGroupLink(groupLink)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND_EXCEPTION
                        , ErrorCode.GROUP_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // GroupMember 조회 - 게스트 토큰
    public GroupMember getGroupMemberByGuestToken(String guestToken) {
        return groupMemberRepository.findByGuestToken(guestToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_INVALID
                        , ErrorCode.JWT_INVALID.getMessage()));
    }

    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND_EXCEPTION
                        , ErrorCode.GAME_NOT_FOUND_EXCEPTION.getMessage()));
    }

    public Travel getTravelById(Long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION
                        , ErrorCode.TRAVEL_NOT_FOUND_EXCEPTION.getMessage()));
    }

    public TravelPlace getTravelPlaceById(Long travelPlaceId) {
        return travelPlaceRepository.findById(travelPlaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAVEL_PLACE_NOT_FOUND_EXCEPTION
                        , ErrorCode.TRAVEL_PLACE_NOT_FOUND_EXCEPTION.getMessage()));
    }
}
