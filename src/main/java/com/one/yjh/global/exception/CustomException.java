package com.one.yjh.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;

    public String getMessage() {
        return errorCode.getMessage();
    }
}
