package com.valuego.notification.application;

import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.notification.api.dto.response.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final GroupMemberRepository groupMemberRepository;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        List<GroupMember> groupMembers = groupMemberRepository.findAllById(event.targetGroupMemberIds());

        for (GroupMember member : groupMembers) {
            if (member.getUser() != null) {
                notificationService.createNotificationForUser(member.getUser().getId(), event.type(), event.targetId());
            } else {
                notificationService.createNotificationForGuest(member.getId(), event.type(), event.targetId());
            }
        }
    }
}
