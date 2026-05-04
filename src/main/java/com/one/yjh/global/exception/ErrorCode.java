package com.one.yjh.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 커스텀 에러 코드<br>
 * 기존의 HTTP 메서드 코드로 얻을 수 있는 에러는 한정되어있어서<br>
 * 각 로직에서 자주 발생하는 에러들을 설정하여 더 자세한 정보를<br>
 * 클라이언트에게 넘겨 오류를 더 쉽게 찾을 수 있도록 구성함
 * */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "U002", "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 가입된 이메일입니다."),

    // 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}