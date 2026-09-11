package com.valuego.effort.api;

import com.valuego.effort.api.dto.request.EffortItemReqDto;
import com.valuego.effort.api.dto.response.EffortItemResDto;
import com.valuego.effort.service.EffortItemService;
import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/effort-items")
@Tag(name = "Effort API", description = "수고 관련 API")
public class EffortItemController {

    private final EffortItemService effortItemService;

    @Operation(summary = "수고 항목 리스트 조회", description = "그룹 내 등록된 기본/커스텀 수고 항목 목록을 조회합니다.")
    @GetMapping
    public ApiResTemplate<List<EffortItemResDto>> getEffortItems(Principal principal,
                                                                 @RequestParam Long groupId,
                                                                 @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        List<EffortItemResDto> effortItemResDtos = effortItemService.getEffortItems(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, effortItemResDtos);
    }

    @Operation(summary = "수고 항목 직접 추가", description = "사용자가 직접 새로운 수고 항목을 추가합니다.")
    @PostMapping
    public ApiResTemplate<EffortItemResDto> createEffortItem(Principal principal,
                                                             @RequestParam Long groupId,
                                                             @Valid @RequestBody EffortItemReqDto effortItemReqDto,
                                                             @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        EffortItemResDto effortItemResDto = effortItemService.createEffortItem(principal, groupId, effortItemReqDto, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.CREATE_SUCCESS, effortItemResDto);
    }

    @Operation(summary = "수고 항목 삭제", description = "직접 추가했던 커스텀 수고 항목을 삭제합니다.")
    @DeleteMapping("/{effortItemId}")
    public ApiResTemplate<Void> deleteEffortItem(Principal principal,
                                                 @RequestParam Long groupId,
                                                 @PathVariable Long effortItemId, @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        effortItemService.deleteEffortItem(principal, groupId, effortItemId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.DELETE_SUCCESS, null);
    }
}
