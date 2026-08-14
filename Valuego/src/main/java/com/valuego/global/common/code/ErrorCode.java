package com.valuego.global.common.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // COMMON
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검사에 실패하였습니다.", "COMMON-001"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러가 발생했습니다.", "COMMON-002"),

    // JWT
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT가 만료되었습니다.", "JWT-001"),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다.", "JWT-002"),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패했습니다.", "JWT-003"),
    JWT_EMPTY(HttpStatus.UNAUTHORIZED, "JWT가 비어있거나 잘못되었습니다.", "JWT-004"),

    // AUTH
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "AUTH-001"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "AUTH-002"),
    KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "카카오 토큰 요청 실패", "AUTH-003"),
    KAKAO_USER_INFO_FAILED(HttpStatus.BAD_GATEWAY, "카카오 사용자 정보 조회 실패", "AUTH-004"),
    KAKAO_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "카카오 계정에 이메일이 없습니다.", "AUTH-005"),
    KAKAO_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "카카오 로그인에 실패했습니다.", "AUTH-006"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", "AUTH-007"),

    // USER
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.", "USER-001"),
    USER_NOTIFICATION_AGREE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자의 동의 항목을 찾을 수 없습니다.", "USER-002"),

    // GROUP
    GROUP_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 그룹을 찾을 수 없습니다.", "GROUP-001"),
    ALREADY_FINISHED_GROUP_EXCEPTION(HttpStatus.BAD_GATEWAY, "여행이 종료된 그룹입니다.", "GROUP-002"),
    NO_REMAINING_MEMBER_COUNT(HttpStatus.BAD_GATEWAY, "그룹의 정원이 가득 찼습니다.", "GROUP-003"),
    GROUP_MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "그룹의 멤버를 찾을 수 없습니다.", "GROUP-004"),

    // STYLE
    STYLE_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "성향을 이미 입력하였습니다.", "STYLE-001"),
    STYLE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "성향을 찾을 수 없습니다.", "STYLE-002"),

    // GAME
    GAME_QUIZ_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "퀴즈를 찾을 수 없습니다.", "GAME-001"),
    GAME_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.", "GAME-002"),
    GAME_QUIZ_CREATE_EXCEPTION(HttpStatus.BAD_GATEWAY, "퀴즈 보기 생성에 실패했습니다.", "GAME-003");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
