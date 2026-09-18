package com.valuego.travel.api.dto.response;

import java.util.List;

public record NaverBlogResDto(
        String keyword,
        String totalReviewUrl,
        Integer totalCount,
        List<NaverBlogItemResDto> reviews
) {
    public record NaverBlogItemResDto(
            String title,       // 블로그 글 제목
            String description, // 블로그 본문 요약
            String bloggerName, // 블로거 이름
            String postDate,    // 작성일
            String link         // 원문 보기 URL
    ) {}
}
