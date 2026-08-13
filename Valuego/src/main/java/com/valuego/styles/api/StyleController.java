package com.valuego.styles.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.styles.api.dto.request.StyleReqDto;
import com.valuego.styles.api.dto.response.StyleInfoResDto;
import com.valuego.styles.service.StyleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups/styles")
@Tag(name = "Style API", description = "그룹 여행 스타일 관련 API")
public class StyleController {

    private final StyleService styleService;

    @Operation(summary = "팀장 여행 스타일 입력", description = "로그인한 사용자(팀장)가 해당 그룹의 여행 스타일을 입력합니다.")
    @PostMapping
    public ApiResTemplate<StyleInfoResDto> createLeaderStyle(Principal principal,
                                                             @RequestParam Long groupId,
                                                             @Valid @RequestBody StyleReqDto styleReqDto) {
        StyleInfoResDto styleInfoResDto = styleService.createLeaderStyle(principal, groupId, styleReqDto);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, styleInfoResDto);
    }

    @Operation(summary = "게스트 여행 스타일 입력", description = "게스트가 자신의 여행 스타일을 입력합니다.")
    @PostMapping("/guest")
    public ApiResTemplate<StyleInfoResDto> createGuestStyle(@CookieValue(value = "guestAccessToken", required = false) String guestToken,
                                                            @Valid @RequestBody StyleReqDto styleReqDto) {
        StyleInfoResDto styleInfoResDto = styleService.createGuestStyle(guestToken, styleReqDto);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, styleInfoResDto);
    }
}
