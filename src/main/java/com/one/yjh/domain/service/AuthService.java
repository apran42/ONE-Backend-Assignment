package com.one.yjh.domain.service;

import com.one.yjh.domain.dto.LoginRequest;
import com.one.yjh.domain.dto.SignupRequest;
import com.one.yjh.domain.dto.TokenResponse;
import com.one.yjh.domain.entity.RefreshToken;
import com.one.yjh.domain.entity.Users;
import com.one.yjh.domain.repository.RefreshTokenRepository;
import com.one.yjh.domain.repository.UsersRepository;
import com.one.yjh.global.config.JwtProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public TokenResponse login(LoginRequest request) {
        // 이메일(아이디)로 사용자 조회
        Users users = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 비밀번호 일치 여부
        if(!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtProvider.createAccessToken(users);
        String refreshToken = jwtProvider.createRefreshToken(users);

        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);


        // 발급한 리프레시 토큰 저장
        refreshTokenRepository.findById(users.getId())
                .ifPresent(refreshTokenRepository::delete); // 이미 리프레시 토큰이 존재하면 삭제

        RefreshToken rToken = refreshTokenRepository.findById(users.getId())
                        .map(existingToken -> {
                            // 기존 토큰 존재
                            return RefreshToken.builder()
                                    .user(users)
                                    .tokenValue(refreshToken)
                                    .expiredAt(expiryDate)
                                    .build();
                        })
                        .orElseGet(() -> RefreshToken.builder()
                                .user(users)
                                .tokenValue(refreshToken)
                                .expiredAt(expiryDate)
                                .build());

        refreshTokenRepository.save(rToken);

        // DTO에 담아 반환
        return new TokenResponse(accessToken, refreshToken);
    }

    // 회원가입
    public void signup(SignupRequest request) {
        Users users = Users.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        usersRepository.save(users);
    }
}
