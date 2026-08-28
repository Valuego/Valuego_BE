package com.valuego.travel.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.api.dto.response.GroupInfoResDto;
import com.valuego.groups.api.dto.response.GroupMemberInfoResDto;
import com.valuego.groups.api.dto.response.GroupStatusResDto;
import com.valuego.groups.entity.Group;
import com.valuego.groups.service.GroupService;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.tourplace.service.AiScheduleService;
import com.valuego.travel.api.dto.response.TravelPlaceInfoResDto;
import com.valuego.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class TravelController {

    private final AiScheduleService aiScheduleService;
    private final GroupService groupService;
    private final TravelService travelService;

    @Operation(summary = "AI 일정 생성", description = "사용자의 그룹별 여행지, 여행 스타일에 따른 AI 추천 일정을 생성합니다.")
    @PostMapping("/ai")
    public ApiResTemplate<TravelScheduleResDto> generateAiScheduleSimple(Principal principal,
                                                                         @RequestParam Long groupId) throws Exception {
        Group group = groupService.getGroup(principal, groupId);

        // 관광공사 api 호출 메소드
        AiScheduleService.TourCandidatesResult candidates = aiScheduleService.fetchTourCandidates(group);

        // ai 일정 생성
        TravelScheduleResDto result = aiScheduleService.generateAndSaveSchedule(
                groupId,
                group,
                candidates.getActivities(),
                candidates.getRestaurants()
        );

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, result);
    }

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
}
