package com.one.yjh.global.exception;

import lombok.Getter;

/**
 * 공통 예외 클래스<br>
 * 미리 정의한 에러 코드를 사용하여 일관된 메시지를 전달함<br>
 * 예외 발생 시점에 ErrorCode를 넘기면 핸들러가 이를 받아 공통 규격으로 변환
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
