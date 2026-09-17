package com.valuego.settlement.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.settlement.api.dto.response.PastSettlementResDto;
import com.valuego.settlement.api.dto.response.SettlementRecapResDto;
import com.valuego.settlement.api.dto.response.SettlementResDto;
import com.valuego.settlement.service.RecapService;
import com.valuego.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
@Tag(name = "Settlement API", description = "정산 관련 API")
public class SettlementController {

    private final SettlementService settlementService;
    private final RecapService recapService;

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

    @Operation(summary = "그룹 리캡 카드 조회", description = "그룹의 총 지출, 수고 가치 총액, 상위 지출 항목 및 정산 정보를 조회합니다.")
    @GetMapping("/recap")
    public ApiResTemplate<SettlementRecapResDto> getSettlementRecap(
            Principal principal,
            @RequestParam Long groupId,
            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {

        SettlementRecapResDto response = recapService.getSettlementRecap(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }

    @Operation(summary = "지난 정산 내역 목록 조회", description = "카카오 로그인 사용자가 참여한 그룹 중 정산 확정이 완료된 지난 정산 내역 목록을 최신순으로 조회합니다.")
    @GetMapping("/history")
    public ApiResTemplate<List<PastSettlementResDto>> getPastSettlements(Principal principal) {
        List<PastSettlementResDto> response = settlementService.getPastSettlements(principal);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, response);
    }
}
