package com.one.yjh.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;   // 요청 성공 여부
    private String code;
    private String message;    // 응답 메시지
    private T data;            // 실제 데이터 (실패 시 null)

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("요청에 성공하였습니다.")
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
