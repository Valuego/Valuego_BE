package com.valuego.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    RETROSPECT_COMPLETE("모든 멤버가 회고 작성을 완료했습니다.", "주고 받은 가치 결과를 확인해보세요."),
    GROUP_JOIN_COMPLETE("모든 멤버가 그룹에 참여했습니다.", "이어서 여행 계획을 마무리하세요.");

    private final String defaultTitle;
    private final String defaultContent;
}
