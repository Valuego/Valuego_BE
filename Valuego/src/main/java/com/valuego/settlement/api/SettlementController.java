package com.valuego.settlement.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.settlement.api.dto.response.SettlementResDto;
import com.valuego.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
@Tag(name = "Settlement API", description = "정산 관련 API")
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "통합 정산표 조회", description = "그룹의 총 지출, 1인당 지출 및 수고 가치가 합산된 멤버별 최종 정산 내역을 조회합니다.")
    @GetMapping
    public ApiResTemplate<SettlementResDto> getIntegratedSettlement(
            Principal principal,
            @RequestParam Long groupId,
            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {

        SettlementResDto response = settlementService.getIntegratedSettlement(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

    @Operation(summary = "정산표 확인 완료 처리", description = "통합 정산표의 '정산표 확인하기' 버튼을 눌러 정산 확정을 처리합니다.")
    @PostMapping("/confirm")
    public ApiResTemplate<Void> confirmSettlement(
            Principal principal,
            @RequestParam Long groupId,
            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {

        settlementService.confirmSettlement(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }
}
