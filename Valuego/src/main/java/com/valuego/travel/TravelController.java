package com.valuego.travel;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.entity.Group;
import com.valuego.groups.service.GroupService;
import com.valuego.tourplace.api.dto.response.TravelScheduleResDto;
import com.valuego.tourplace.service.AiScheduleService;
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
}