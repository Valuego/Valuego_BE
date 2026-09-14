package com.valuego.styles.service;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.exception.EntityFinderException;
import com.valuego.global.common.exception.ValidMemberException;
import com.valuego.groups.entity.Enum.MemberRole;
import com.valuego.groups.entity.Group;
import com.valuego.groups.entity.GroupMember;
import com.valuego.groups.entity.repository.GroupMemberRepository;
import com.valuego.styles.api.dto.request.StyleReqDto;
import com.valuego.styles.api.dto.response.MyStyleCardResDto;
import com.valuego.styles.api.dto.response.StyleAiResDto;
import com.valuego.styles.api.dto.response.StyleInfoResDto;
import com.valuego.styles.entity.Enum.BudgetType;
import com.valuego.styles.entity.Enum.FoodType;
import com.valuego.styles.entity.Style;
import com.valuego.styles.entity.repository.StyleRepository;
import com.valuego.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StyleService {

    private final StyleRepository styleRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EntityFinderException entityFinderException;
    private final ValidMemberException validMemberException;
    private final StyleGeminiService styleGeminiService;

    // 팀장
    @Transactional
    public StyleInfoResDto createLeaderStyle(Principal principal, Long groupId, StyleReqDto styleReqDto) {
        User user = entityFinderException.getUserFromPrincipal(principal);
        Group group = entityFinderException.getGroupById(groupId);

        // 해당 그룹의 팀장인지 확인
        if (!group.getLeader().getId().equals(user.getId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_EXCEPTION,
                    "해당 그룹의 팀장만 여행 스타일을 입력할 수 있습니다."
            );
        }

        // 해당 그룹의 팀장 GroupMember 조회
        GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
                () -> new BusinessException(ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        return saveStyle(groupMember, styleReqDto);
    }

    // 게스트
    @Transactional
    public StyleInfoResDto createGuestStyle(String guestToken, StyleReqDto styleReqDto) {
        GroupMember groupMember = entityFinderException.getGroupMemberByGuestToken(guestToken);

        // 게스트 토큰 만료 확인
        if (groupMember.getGuestTokenExpiresAt() == null ||
                LocalDateTime.now().isAfter(
                        groupMember.getGuestTokenExpiresAt()
                )) {
            throw new BusinessException(
                    ErrorCode.JWT_EXPIRED,
                    ErrorCode.JWT_EXPIRED.getMessage()
            );
        }

        return saveStyle(groupMember, styleReqDto);
    }

    // 스타일 저장
    private StyleInfoResDto saveStyle(GroupMember groupMember, StyleReqDto styleReqDto) {
        // 이미 입력한 경우
        if (styleRepository.existsByGroupMember(groupMember)) {
            throw new BusinessException(
                    ErrorCode.STYLE_ALREADY_EXISTS,
                    ErrorCode.STYLE_ALREADY_EXISTS.getMessage()
            );
        }

        Style style = new Style(
                groupMember,
                styleReqDto.budgetType(),
                styleReqDto.foodType(),
                styleReqDto.activityIntensity()
        );

        styleRepository.save(style);

        // 성향 입력 완료
        groupMember.completePreference();

        return StyleInfoResDto.from(style);
    }

    // 팀장 성향 그룹별 조회
    @Transactional(readOnly = true)
    public MyStyleCardResDto getLeaderStyleCard(Long groupId, Principal principal, String guestToken) {
        Group group = entityFinderException.getGroupById(groupId);
        GroupMember member = validMemberException.validateGroupMember(principal, guestToken, group);

        if (member.getMemberRole() != MemberRole.LEADER) {
            throw new BusinessException(ErrorCode.FORBIDDEN_EXCEPTION, "팀장(카카오 로그인 사용자)만 접근할 수 있는 기능입니다.");
        }

        Style style = styleRepository.findByGroupMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.STYLE_NOT_FOUND_EXCEPTION
                        , ErrorCode.STYLE_ALREADY_EXISTS.getMessage()));

        StyleAiResDto styleAiResDto = styleGeminiService.analyzeLeaderStyle(style);

        String activityText = getActivityText(style.getActivityIntensity());
        String budgetText = getBudgetText(style.getBudgetType());
        String foodText = getFoodText(style.getFoodType());

        return MyStyleCardResDto.of(
                style.getId(),
                groupId,
                member.getId(),
                styleAiResDto.dnaTitle(),
                styleAiResDto.dnaDescription(),
                styleAiResDto.tags(),
                activityText,
                budgetText,
                foodText,
                style.getBudgetType(),
                style.getFoodType(),
                style.getActivityIntensity()
        );
    }

    private String getActivityText(Integer intensity) {
        if (intensity == null) return "적당히";
        if (intensity >= 4) return "알차게";
        if (intensity <= 2) return "여유롭게";
        return "적당히";
    }

    private String getBudgetText(BudgetType budgetType) {
        if (budgetType == null) return "적당히";
        return switch (budgetType) {
            case ECONOMICAL -> "알뜰하게";
            case LUXURY -> "플렉스";
            default -> "적당히";
        };
    }

    private String getFoodText(FoodType foodType) {
        if (foodType == null) return "상관없음";
        return switch (foodType) {
            case KOREAN -> "한식";
            case JAPANESE -> "일식";
            case CHINESE -> "중식";
            default -> "상관없음";
        };
    }
}
