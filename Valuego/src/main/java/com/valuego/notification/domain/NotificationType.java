package com.valuego.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    RETROSPECT_COMPLETE("'%s' 그룹의 모든 멤버가 회고 작성을 완료했습니다.", "주고 받은 가치 결과를 확인해보세요."),
    GROUP_JOIN_COMPLETE("모든 멤버가 '%s' 그룹에 참여했습니다.", "이어서 여행 계획을 마무리하세요.");

    private final String defaultTitle;
    private final String defaultContent;

    /**
     * 동적 인자를 받아 제목을 생성 (인자가 없으면 기본 문구 반환)
     */
    public String generateTitle(Object... args) {
        if (args == null || args.length == 0) {
            return this.defaultTitle;
        }
        return String.format(this.defaultTitle, args);
    }
}
