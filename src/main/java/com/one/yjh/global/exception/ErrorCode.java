package com.one.yjh.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 가입된 이메일입니다."),
    WRONG_PASSWORD(HttpStatus.CONFLICT, "U003", "비밀번호가 일치하지 않습니다."),

    // 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "인증 토큰이 누락되었습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "지원하지 않는 형식의 토큰입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "토큰이 비어있습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}