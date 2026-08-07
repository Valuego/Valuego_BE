package com.valuego.users.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.users.api.dto.response.UserInfoResDto;
import com.valuego.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "사용자 관련 API - MyPage")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자가 자신의 계정 정보를 조회합니다. (본인만 조회 가능, 이메일 포함)")
    @GetMapping("/profile")
    public ApiResTemplate<UserInfoResDto> getUserInfo(Principal principal) {
        UserInfoResDto userInfoResDto = userService.getUserInfo(principal);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userInfoResDto);
    }
}
