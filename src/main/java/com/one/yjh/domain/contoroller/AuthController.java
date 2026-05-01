package com.one.yjh.domain.contoroller;

import com.one.yjh.domain.dto.LoginRequest;
import com.one.yjh.domain.dto.SignupRequest;
import com.one.yjh.domain.dto.TokenResponse;
import com.one.yjh.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);

        return ResponseEntity.ok(tokenResponse);
    }

    // 회원 가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
