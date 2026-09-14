package com.valuego.notification.api;

import com.valuego.global.common.code.SuccessCode;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.notification.api.dto.response.NotificationResDto;
import com.valuego.notification.application.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "SSE 구독", description = "SSE 구독 생성, 로딩중 화면이 정상입니다." )
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Principal principal,
                                                @CookieValue(name = "guestAccessToken", required = false) String guestToken) {
        SseEmitter emitter = notificationService.subscribe(principal, guestToken);
        return emitter;
    }

    @Operation(summary = "알림 전체 목록 조회", description = "사용자가 알림 전체 리스트를 조회합니다.")
    @GetMapping
    public ApiResTemplate<List<NotificationResDto>> getNotificationList(Principal principal,
                                                                        @CookieValue(name = "guestAccessToken", required = false) String guestToken) {
        List<NotificationResDto> notificationList = notificationService.getNotificationList(principal, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, notificationList);
    }

    @Operation(summary = "알림 읽음 처리", description = "사용자가 상세 알림을 읽음 처리합니다.")
    @PatchMapping("/{notificationId}/read")
    public ApiResTemplate<NotificationResDto> readNotification(@PathVariable Long notificationId,
                                                               Principal principal,
                                                               @CookieValue(name = "guestAccessToken", required = false) String guestToken) {
        NotificationResDto notificationResDto = notificationService.readNotification(notificationId, principal, guestToken);
        return ApiResTemplate.successResponse(SuccessCode.SUCCESS, notificationResDto);
    }
}
