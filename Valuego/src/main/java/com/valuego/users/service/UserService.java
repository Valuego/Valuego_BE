package com.valuego.users.service;

import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.users.api.dto.request.UserAgreeUpdateReqDto;
import com.valuego.users.api.dto.request.UserInfoUpdateReqDto;
import com.valuego.users.api.dto.response.UserInfoResDto;
import com.valuego.users.entity.User;
import com.valuego.users.entity.UserNotificationAgree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final EntityFinderException entityFinderException;

    // 마이페이지 - 내 계정 정보 조회
    public UserInfoResDto getUserInfo(Principal principal) {
        User user = entityFinderException.getUserFromPrincipal(principal);
        return UserInfoResDto.from(user);
    }

    // 마이페이지 - 내 정보 수정
    @Transactional
    public UserInfoResDto updateUserInfo(Principal principal, UserInfoUpdateReqDto userInfoUpdateReqDto) {
        User user = entityFinderException.getUserFromPrincipal(principal);

        user.updateMemberColor(userInfoUpdateReqDto.memberColor());
        user.updateNickname(userInfoUpdateReqDto.nickname());

        return UserInfoResDto.from(user);
    }

    // 마이페이지 - 동의 항목 여부 수정
    @Transactional
    public UserInfoResDto updateUserAgree(
            Principal principal,
            UserAgreeUpdateReqDto request
    ) {
        User user = entityFinderException.getUserFromPrincipal(principal);
        UserNotificationAgree agree = entityFinderException.getUserNotificationAgree(user);

        agree.updateNotificationAgree(
                request.notifyComments(),
                request.notifyReminders(),
                request.notifySettlement(),
                request.notifyMarketing()
        );

        return UserInfoResDto.from(user);
    }
}
