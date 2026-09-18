package com.valuego.travel.service;

import com.valuego.travel.api.dto.response.NaverBlogResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverBlogService {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BLOG_SEARCH_URL = "https://naverapihub.apigw.ntruss.com/search/v1/blog";
    private static final String DEFAULT_KEYWORD = "여행지";
    private static final String FALLBACK_DATE = "2026.06";

    // 장소명, 주소 기반 네이버 블로그 검색 API 호출 (상위 3개 + 블로그 탭 URL)
    public NaverBlogResDto getPlaceBlogReviews(String name, String address) {
        String searchKeyword = buildSearchKeyword(name, address);
        List<NaverBlogResDto.NaverBlogItemResDto> reviewItems = new ArrayList<>();
        int totalCount = 0;

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(BLOG_SEARCH_URL)
                    .queryParam("query", searchKeyword)
                    .queryParam("display", 3)
                    .queryParam("sort", "date")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.set("X-NCP-APIGW-API-KEY", clientSecret);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Map.class);

            if (response.getBody() != null && response.getBody().get("items") instanceof List<?> items) {
                if (response.getBody().get("total") instanceof Number total) {
                    totalCount = total.intValue();
                }

                for (Object itemObj : items) {
                    if (itemObj instanceof Map<?, ?> item) {
                        String rawTitle = getStringOrDefault(item.get("title"), "");
                        String rawDescription = getStringOrDefault(item.get("description"), "");
                        String bloggerName = getStringOrDefault(item.get("bloggername"), "");
                        String link = getStringOrDefault(item.get("link"), "");
                        String postdate = getStringOrDefault(item.get("postdate"), "");

                        reviewItems.add(new NaverBlogResDto.NaverBlogItemResDto(
                                stripHtmlTags(rawTitle),
                                stripHtmlTags(rawDescription),
                                bloggerName,
                                formatDate(postdate),
                                link
                        ));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Naver Blog Search API Error for keyword '{}': {}", searchKeyword, e.getMessage());
        }

        String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        String totalReviewUrl = "https://search.naver.com/search.naver?ssc=tab.blog.all&query=" + encodedKeyword;

        return new NaverBlogResDto(
                searchKeyword,
                totalReviewUrl,
                totalCount,
                reviewItems
        );
    }

    private String getStringOrDefault(Object obj, String defaultValue) {
        return (obj instanceof String str) ? str : defaultValue;
    }

    private String stripHtmlTags(String input) {
        if (input == null || input.isBlank()) return "";
        return input.replaceAll("<[^>]*>", "")
                .replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&apos;", "'");
    }

    private String formatDate(String postdate) {
        if (postdate == null || postdate.length() < 8) return FALLBACK_DATE;
        try {
            LocalDate date = LocalDate.parse(postdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } catch (DateTimeParseException e) {
            return FALLBACK_DATE;
        }
    }

    private String buildSearchKeyword(String name, String address) {
        StringBuilder sb = new StringBuilder();

        if (address != null && !address.isBlank()) {
            String[] addressParts = address.split(" ");
            if (addressParts.length >= 2) {
                // 예: "부산" + "부산진구" 또는 "서면"
                sb.append(addressParts[0]).append(" ");
                sb.append(addressParts[1]).append(" ");
            }
        }

        sb.append(name).append(" ");

        return sb.toString().trim();
    }

}
