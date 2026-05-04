package com.one.yjh.domain.service;

import com.one.yjh.domain.dto.LoginRequest;
import com.one.yjh.domain.dto.SignupRequest;
import com.one.yjh.domain.dto.TokenResponse;
import com.one.yjh.domain.entity.RefreshToken;
import com.one.yjh.domain.entity.Users;
import com.one.yjh.domain.repository.RefreshTokenRepository;
import com.one.yjh.domain.repository.UsersRepository;
import com.one.yjh.global.config.JwtProvider;

import com.one.yjh.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.one.yjh.global.exception.ErrorCode.*;

/**
 * 사용자의 로그인과 회원가입을 담당하는 서비스 계층<br>
 * JWT 기반의 인증 시스템을 구축하였고, <br>
 * 로그인 시의 비즈니스 로직에서 발생하는 커스텀 에러를 던지도록 하여 <br>
 * 규격화된 정보를 전달
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     *
     * @param request
     * dto를 통해 받은 request 객체에서 이메일(아이디 대용)과 비밀 번호 정보를 활용해 <br>
     * 유저 존재 여부와 비밀 번호 일치 여부를 검사 <br>
     * JWT 원칙에 맞게 로그인 시마다 이미 받은 리프레시 토큰이 있다면 삭제 후 새로운 토큰 저장
     */
    // 로그인
    public TokenResponse login(LoginRequest request) {
        // 이메일(아이디)로 사용자 조회
        Users users = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 비밀번호 일치 여부
        if(!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new CustomException(WRONG_PASSWORD);
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

    /**
     *
     * @param request
     * request 객체를 통해 받은 새로운 유저 정보를 저장
     */
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
