package com.valuego.comment.api.dto.response;

import com.valuego.comment.entity.Comment;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentListResDto(
        long commentCount,
        List<CommentInfoResDto> comments
) {
    public static CommentListResDto of(List<Comment> comments) {
        List<CommentInfoResDto> commentInfoList = comments.stream()
                .map(CommentInfoResDto::from)
                .toList();

        return CommentListResDto.builder()
                .commentCount(comments.size())
                .comments(commentInfoList)
                .build();
    }
}
