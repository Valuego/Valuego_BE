package com.valuego.users.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.users.api.dto.response.UserScheduleResDto;
import com.valuego.users.api.dto.response.UserTimelineResDto;
import com.valuego.users.service.UserTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users/timeline")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API - MyPage")
public class UserTimelineController {

    private final UserTimelineService userTimelineService;

    @Operation(summary = "타임라인 조회", description = "그룹에 참여한 사용자가 타임라인 리스트를 조회합니다.")
    @GetMapping
    public ApiResTemplate<UserTimelineResDto> getTimeLine(Principal principal,
                                                          @RequestParam Long groupId,
                                                          @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        UserTimelineResDto userTimelineResDto = userTimelineService.getTimeline(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userTimelineResDto);
    }

    @Operation(summary = "남은 일정 조회", description = "그룹에 참여한 사용자가 남은 일정 리스트를 조회합니다.")
    @GetMapping("/remaining")
    public ApiResTemplate<UserScheduleResDto> getRemainingSchedule(Principal principal,
                                                                   @RequestParam Long groupId,
                                                                   @CookieValue(value = "guestAccessToken", required = false) String guestToken) {
        UserScheduleResDto userScheduleResDto = userTimelineService.getRemainingSchedule(principal, groupId, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, userScheduleResDto);
    }
}
