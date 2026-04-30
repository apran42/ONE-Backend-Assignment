package com.one.yjh.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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