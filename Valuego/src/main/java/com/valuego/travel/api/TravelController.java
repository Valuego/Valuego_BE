package com.valuego.travel.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.api.dto.response.GroupStatusResDto;
import com.valuego.groups.entity.Group;
import com.valuego.groups.service.GroupService;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.tourplace.service.AiScheduleService;
import com.valuego.travel.api.dto.request.AiScheduleUpdateReqDto;
import com.valuego.travel.api.dto.request.TravelPlaceCreateReqDto;
import com.valuego.travel.api.dto.request.TravelPlaceUpdateReqDto;
import com.valuego.travel.api.dto.response.AiScheduleUpdateResDto;
import com.valuego.travel.api.dto.response.TravelPlaceInfoResDto;
import com.valuego.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "AI Schedule API", description = "AI 일정 관련 API")
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

    @Operation(summary = "AI 일정 수정 요청", description = "사용자의 피드백 프롬프트(예: '더 느긋하게')를 받아 AI 제안 일정을 반환합니다. (DB 미반영)")
    @PostMapping("/ai/suggest")
    public ApiResTemplate<AiScheduleUpdateResDto> suggestAiScheduleUpdate(Principal principal,
                                                                          @RequestBody AiScheduleUpdateReqDto reqDto,
                                                                          @CookieValue(value = "guestAccessToken", required = false) String guestToken) throws Exception {
        AiScheduleUpdateResDto aiScheduleUpdateResDto = travelService.suggestAiScheduleUpdate(principal, reqDto, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, aiScheduleUpdateResDto);
    }

    @Operation(summary = "AI 제안 일정 확정", description = "AI가 제안한 수정 일정(AiScheduleUpdateResDto)을 최종 선택하여 DB를 갱신합니다.")
    @PostMapping("/ai/apply")
    public ApiResTemplate<TravelScheduleResDto> applyAiScheduleUpdate(Principal principal,
                                                                      @RequestParam Long groupId,
                                                                      @RequestBody AiScheduleUpdateResDto suggestedSchedule,
                                                                      @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        TravelScheduleResDto travelScheduleResDto = travelService.applyAiScheduleUpdate(principal, groupId, suggestedSchedule, guestToken);

        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, travelScheduleResDto);
    }
}
