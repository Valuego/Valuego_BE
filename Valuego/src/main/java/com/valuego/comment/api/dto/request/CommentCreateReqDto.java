package com.valuego.comment.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateReqDto(
        @NotBlank(message = "의견 내용을 입력해주세요.")
        @Size(max = 500, message = "의견은 최대 500자까지 작성 가능합니다.")
        String content
) {
}
