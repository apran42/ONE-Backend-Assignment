package com.one.yjh.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 모든 API 응답에 사용하는 공통 응답 규격<br>
 * 클라이언트와의 일관된 통신을 위해 성공/실패 모두 같은 형식을 따르도록 규격화하였음<br>
 * success 변수를 톰해 성공 여부를 전달하고,<br>
 * 제네릭 타입의 유연한 데이터를 넘기도록 구현하였음
 * */
@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;   // 요청 성공 여부
    private String message;    // 응답 메시지
    private T data;            // 실제 데이터 (실패 시 null)

    // 성공 응답
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
