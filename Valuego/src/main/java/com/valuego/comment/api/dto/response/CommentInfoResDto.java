package com.valuego.comment.api.dto.response;

import com.valuego.comment.entity.Comment;
import com.valuego.groups.entity.Enum.MemberColor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentInfoResDto (
    Long commentId,
    Long userId,
    String nickname,
    MemberColor memberColor,
    String profileImageUrl,
    String content,
    LocalDateTime createdAt
) {
    public static CommentInfoResDto from(Comment comment) {
        Long userId = (comment.getUser() != null) ? comment.getUser().getId() : null;
        String profileImageUrl = (comment.getUser() != null) ? comment.getUser().getProfileImageUrl() : null;

        return CommentInfoResDto.builder()
                .commentId(comment.getId())
                .userId(userId)
                .nickname(comment.getGroupMember() != null ? comment.getGroupMember().getMemberName() : "알 수 없음")
                .memberColor(comment.getGroupMember() != null ? comment.getGroupMember().getMemberColor() : null)
                .profileImageUrl(profileImageUrl)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
