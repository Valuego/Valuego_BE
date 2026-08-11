package com.valuego.oauth2.kakao.service;

import com.google.gson.Gson;
import com.valuego.global.common.code.ErrorCode;
import com.valuego.global.common.exception.BusinessException;
import com.valuego.global.common.template.ApiResTemplate;
import com.valuego.groups.entity.Enum.MemberColor;
import com.valuego.oauth2.kakao.api.dto.KakaoToken;
import com.valuego.oauth2.kakao.api.dto.KakaoUserInfo;
import com.valuego.oauth2.kakao.api.dto.LoginInfoResDto;
import com.valuego.users.entity.SocialType;
import com.valuego.users.entity.User;
import com.valuego.users.entity.UserNotificationAgree;
import com.valuego.users.entity.UserRole;
import com.valuego.users.entity.repository.UserNotificationAgreeRepository;
import com.valuego.users.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserNotificationAgreeRepository userNotificationAgreeRepository;

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

    // 카카오에서 전달해 준 인가 코드로 액세스 토큰 리다이렉트
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED
                    , ErrorCode.KAKAO_TOKEN_REQUEST_FAILED.getMessage());
        }

        try {
            Gson gson = new Gson();
            KakaoToken tokenResponse = gson.fromJson(response.getBody(), KakaoToken.class);
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAILED
                    , ErrorCode.KAKAO_LOGIN_FAILED.getMessage());
        }
    }

    // 카카오 사용자 정보 조회
    public KakaoUserInfo getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED,
                    ErrorCode.KAKAO_USER_INFO_FAILED.getMessage());
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(responseEntity.getBody(), KakaoUserInfo.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_LOGIN_FAILED,
                    ErrorCode.KAKAO_LOGIN_FAILED.getMessage());
        }
    }

    // 전체 프로세스
    public User processLogin(String code) {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserInfo kakaoUserInfo = getUserInfoFromKakao(accessToken);

        if (kakaoUserInfo.getKakaoAccount() == null ||
                kakaoUserInfo.getKakaoAccount().getEmail() == null) {
            throw new BusinessException(
                    ErrorCode.KAKAO_EMAIL_NOT_FOUND,
                    ErrorCode.KAKAO_EMAIL_NOT_FOUND.getMessage()
            );
        }

        return userRepository.findByEmail(kakaoUserInfo.getKakaoAccount().getEmail())
                .orElseGet(() -> createKakaoUser(kakaoUserInfo));
    }

    // 유저 생성
    private User createKakaoUser(KakaoUserInfo kakaoUserInfo) {
        User user = userRepository.save(
                User.builder()
                        .email(kakaoUserInfo.getKakaoAccount().getEmail())
                        .profileImageUrl(
                                kakaoUserInfo.getKakaoAccount()
                                        .getProfile()
                                        .getProfileImageUrl()
                        )
                        .nickname(kakaoUserInfo.getProperties().getNickname())
                        .socialType(SocialType.KAKAO)
                        .userRole(UserRole.LEADER)
                        .memberColor(MemberColor.BLUE)
                        .build()
        );

        // 알림 동의 항목 true로 저장
        userNotificationAgreeRepository.save(
                UserNotificationAgree.createUserAgree(user)
        );

        return user;
    }

    // 테스트 계정 accessToken 발급
    public ResponseEntity<ApiResTemplate<LoginInfoResDto>> testLogin() {
        User testUser = userRepository.findByEmail("test@valuego.com")
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage()));

        return refreshTokenService.loginSuccess(testUser);
    }
}
