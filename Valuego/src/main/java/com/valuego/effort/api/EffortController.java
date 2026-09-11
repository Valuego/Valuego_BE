package com.valuego.effort.api;

import com.valuego.effort.api.dto.request.EffortReqDto;
import com.valuego.effort.api.dto.response.EffortInfoResDto;
import com.valuego.effort.api.dto.response.EffortResultResDto;
import com.valuego.effort.service.EffortService;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/efforts")
@Tag(name = "Effort API", description = "수고 관련 API")
public class EffortController {

    private final EffortService effortService;

    @Operation(summary = "수고 회고 생성", description = "특정 멤버에게 전달할 수고 가치 금액과 한마디를 입력하여 제출합니다.")
    @PostMapping
    public ApiResTemplate<EffortInfoResDto> createEffort(Principal principal,
                                                         @Valid @RequestBody EffortReqDto effortReqDto,
                                                         @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        EffortInfoResDto effortInfoResDto = effortService.createEffort(principal, effortReqDto, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, effortInfoResDto);
    }

    @Operation(summary = "최종 수고 결과 조회", description = "특정 멤버가 전달받은 수고 보상 금액의 중간값과 친구들의 한마디 리스트를 조회합니다.")
    @GetMapping("/result")
    public ApiResTemplate<EffortResultResDto> getEffortResult(Principal principal,
                                                              @RequestParam Long groupId,
                                                              @RequestParam Long targetMemberId,
                                                              @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        EffortResultResDto effortResultResDto = effortService.getEffortResult(principal, groupId, targetMemberId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, effortResultResDto);
    }
}
