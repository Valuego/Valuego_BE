package com.valuego.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.template.ApiResTemplate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        try {
            if (uri.startsWith("/api/v1/login")
                    || uri.startsWith("/swagger-ui")
                    || uri.startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean isAuthenticated = false;

            // 1. AccessToken 검사 (카카오 유저 쿠키 검사)
            String accessToken = resolveToken(request);
            if (StringUtils.hasText(accessToken)) {
                try {
                    if (jwtTokenProvider.validateToken(accessToken)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        isAuthenticated = true;
                    }
                } catch (Exception ignored) {
                }
            }

            // 2. 카카오 로그인(AccessToken)이 없거나 유효하지 않은 경우 GuestAccessToken 검사
            if (!isAuthenticated) {
                String guestToken = resolveGuestToken(request);
                if (StringUtils.hasText(guestToken)) {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
                    Authentication guestAuthentication = new UsernamePasswordAuthenticationToken(guestToken, "", authorities);
                    SecurityContextHolder.getContext().setAuthentication(guestAuthentication);
                }
            }

            filterChain.doFilter(request, response);

        } catch (BusinessException ex) {
            setErrorResponse(response, ex.getErrorCode());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        // 쿠키 추출
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 헤더(Guest-Token), 쿼리파라미터(guestToken), 또는 쿠키(guestAccessToken) 추출
    private String resolveGuestToken(HttpServletRequest request) {
        // 1. Header 확인
        String guestToken = request.getHeader("Guest-Token");
        if (StringUtils.hasText(guestToken)) {
            return guestToken;
        }

        // 2. Query Parameter 확인
        guestToken = request.getParameter("guestToken");
        if (StringUtils.hasText(guestToken)) {
            return guestToken;
        }

        // 3. Cookie 확인 (guestAccessToken 명칭 적용)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guestAccessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatusCode());
        response.setContentType("application/json;charset=UTF-8");

        ApiResTemplate<?> body = ApiResTemplate.errorResponse(errorCode, errorCode.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
