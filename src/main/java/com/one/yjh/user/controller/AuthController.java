package com.one.yjh.user.controller;

import com.one.yjh.user.dto.request.LoginRequest;
import com.one.yjh.user.dto.request.SignupRequest;
import com.one.yjh.user.dto.response.TokenResponse;
import com.one.yjh.user.service.AuthService;
import com.one.yjh.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 비즈니스 로직의 엔드 포인트
 * 로그인과 회원가입 시에 호출하는 엔드포인트로<br>
 * 핵심 로직은 서비스 계층에 숨기고 클라이언트에서는<br>
 * 엔드포인트만 호출하여 핵심 기능을 수행할 수 있도록 설계
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인 API
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);

        return ApiResponse.success("로그인되었습니다", tokenResponse);
    }

    // 회원 가입 API
    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ApiResponse.success("회원가입이 완료되었습니다.",null);
    }
}
