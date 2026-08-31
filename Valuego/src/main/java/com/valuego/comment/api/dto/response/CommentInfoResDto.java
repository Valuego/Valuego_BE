package com.valuego.comment.api.dto.response;

import com.valuego.comment.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentInfoResDto (
    Long commentId,
    Long userId,
    String nickname,
    String profileImageUrl,
    String content,
    LocalDateTime createdAt
) {
    public static CommentInfoResDto from(Comment comment) {
        return CommentInfoResDto.builder()
                .commentId(comment.getId())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
