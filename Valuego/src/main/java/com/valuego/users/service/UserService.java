package com.valuego.users.service;

import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.users.api.dto.response.UserInfoResDto;
import com.valuego.users.entity.User;
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
}
