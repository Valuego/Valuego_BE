package com.valuego.travel.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.api.dto.response.GroupStatusResDto;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.travel.api.dto.request.TravelPlaceCreateReqDto;
import com.valuego.travel.api.dto.request.TravelPlaceUpdateReqDto;
import com.valuego.travel.api.dto.response.TravelPlaceInfoResDto;
import com.valuego.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Schedule API", description = "일정 관련 API")
@RequestMapping("/api/v1/schedules")
public class TravelScheduleController {

    private final TravelService travelService;

    @Operation(summary = "그룹별 전체 일정 조회", description = "로그인한 사용자가 그룹별 전체 일정을 조회합니다.")
    @GetMapping("/all")
    public ApiResTemplate<TravelScheduleResDto> getAllSchedule(Principal principal,
                                                               @RequestParam Long groupId,
                                                               @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        TravelScheduleResDto travelScheduleResDto = travelService.getAllSchedule(principal, groupId, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, travelScheduleResDto);
    }

    @Operation(summary = "그룹별 상세 일정 조회", description = "로그인한 사용자가 그룹별 상세 일정 정보를 조회합니다.")
    @GetMapping("/detail")
    public ApiResTemplate<TravelPlaceInfoResDto> getDetailSchedule(Principal principal,
                                                                   @RequestParam Long travelPlaceId,
                                                                   @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        TravelPlaceInfoResDto travelPlaceInfoResDto = travelService.getDetailSchedule(principal, travelPlaceId, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, travelPlaceInfoResDto);
    }

    @Operation(summary = "일정 확정", description = "일정을 확정합니다. 그룹 상태가 CONFIRMED로 변경됩니다.")
    @PatchMapping("/confirm")
    public ApiResTemplate<GroupStatusResDto> confirmSchedule(Principal principal,
                                                             @RequestParam Long groupId,
                                                             @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        GroupStatusResDto groupStatusResDto = travelService.confirmSchedule(principal, groupId, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, groupStatusResDto);
    }

    @Operation(summary = "장소 직접 추가", description = "로그인한 사용자가 새로운 커스텀 장소(이름, 시간, 참고링크, 순서)를 추가합니다.")
    @PostMapping("/places")
    public ApiResTemplate<TravelPlaceInfoResDto> createCustomPlace(Principal principal,
                                                  @RequestBody TravelPlaceCreateReqDto travelPlaceCreateReqDto,
                                                  @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        TravelPlaceInfoResDto travelPlaceInfoResDto = travelService.createCustomPlace(principal, travelPlaceCreateReqDto, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, travelPlaceInfoResDto);
    }

    @Operation(summary = "장소 정보 수정", description = "로그인한 사용자가 장소명, 시간, 참고링크를 수정합니다. 커스텀 장소, ai 일정 모두 수정 가능")
    @PatchMapping("/places")
    public ApiResTemplate<TravelPlaceInfoResDto> updatePlace(Principal principal,
                                            @RequestParam Long travelPlaceId,
                                            @RequestBody TravelPlaceUpdateReqDto reqDto,
                                            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        TravelPlaceInfoResDto travelPlaceInfoResDto = travelService.updatePlace(principal, travelPlaceId, reqDto, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, travelPlaceInfoResDto);
    }

    @Operation(summary = "장소 정보 삭제", description = "로그인한 사용자가 선택한 장소를 일정에서 삭제합니다.")
    @DeleteMapping("/places")
    public ApiResTemplate<String> deletePlace(Principal principal,
                                            @RequestParam Long travelPlaceId,
                                            @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        travelService.deletePlace(principal, travelPlaceId, guestToken);

        return ApiResTemplate.successWithNoContent(SuccessCode.DELETE_SUCCESS);
    }
}
