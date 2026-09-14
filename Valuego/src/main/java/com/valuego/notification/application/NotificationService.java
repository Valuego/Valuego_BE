package com.valuego.notification.application;

import com.valuego.global.common.exception.EntityFinderException;

import com.valuego.groups.entity.GroupMember;
import com.valuego.notification.api.dto.response.NotificationResDto;
import com.valuego.notification.domain.Notification;
import com.valuego.notification.domain.NotificationType;
import com.valuego.notification.domain.repository.NotificationRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;
    private final EntityFinderException entityFinderException;

    // SSE 구독
    public SseEmitter subscribe(Principal principal, String guestToken) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        String emitterKey = getEmitterKey(principal, guestToken);
        return sseEmitters.addEmitter(emitterKey, emitter);
    }

    // 회원용 알림 생성 및 SSE 전송
    @Transactional
    public void createNotificationForUser(Long userId, NotificationType type, Long groupId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(type.getDefaultTitle())
                .content(type.getDefaultContent())
                .groupId(groupId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        sseEmitters.sendToClient("U_" + userId, NotificationResDto.from(notification));
    }

    // 게스트용 알림 생성 및 SSE 전송
    @Transactional
    public void createNotificationForGuest(Long guestGroupMemberId, NotificationType type, Long groupId) {
        Notification notification = Notification.builder()
                .guestGroupMemberId(guestGroupMemberId)
                .type(type)
                .title(type.getDefaultTitle())
                .content(type.getDefaultContent())
                .groupId(groupId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        sseEmitters.sendToClient("G_" + guestGroupMemberId, NotificationResDto.from(notification));
    }

    // 알림 전체 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationResDto> getNotificationList(Principal principal, String guestToken) {
        if (principal != null) {
            User user = entityFinderException.getUserFromPrincipal(principal);
            return notificationRepository.findByUserIdOrderByNotificationCreatedAtDesc(user.getId())
                    .stream()
                    .map(NotificationResDto::from)
                    .toList();
        }

        if (guestToken != null && !guestToken.isBlank()) {
            GroupMember guestMember = entityFinderException.getGroupMemberByGuestToken(guestToken);
            return notificationRepository.findByGuestGroupMemberIdOrderByNotificationCreatedAtDesc(guestMember.getId())
                    .stream()
                    .map(NotificationResDto::from)
                    .toList();
        }

        throw new SecurityException("인증 정보가 유효하지 않습니다.");
    }

    // 알림 읽음 처리
    @Transactional
    public NotificationResDto readNotification(Long notificationId, Principal principal, String guestToken) {
        Notification notification = entityFinderException.getNotificationById(notificationId);

        if (principal != null) {
            User user = entityFinderException.getUserFromPrincipal(principal);
            if (notification.getUserId() == null || !notification.getUserId().equals(user.getId())) {
                throw new SecurityException("본인의 알림만 조회할 수 있습니다.");
            }
        }

        else if (guestToken != null && !guestToken.isBlank()) {
            GroupMember guestMember = entityFinderException.getGroupMemberByGuestToken(guestToken);
            if (notification.getGuestGroupMemberId() == null || !notification.getGuestGroupMemberId().equals(guestMember.getId())) {
                throw new SecurityException("본인의 알림만 조회할 수 있습니다.");
            }
        }
        else {
            throw new SecurityException("인증 정보가 유효하지 않습니다.");
        }

        notification.read();
        return NotificationResDto.from(notification);
    }

    // Emitter Key 추출 (회원: U_userId / 게스트: G_groupMemberId)
    private String getEmitterKey(Principal principal, String guestToken) {
        if (principal != null) {
            User user = entityFinderException.getUserFromPrincipal(principal);
            return "U_" + user.getId();
        } else if (guestToken != null && !guestToken.isBlank()) {
            GroupMember guestMember = entityFinderException.getGroupMemberByGuestToken(guestToken);
            return "G_" + guestMember.getId();
        }
        throw new SecurityException("인증 정보가 유효하지 않습니다.");
    }
}
