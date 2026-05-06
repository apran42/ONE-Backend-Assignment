package com.one.yjh.domain.service;

import com.one.yjh.domain.dto.*;
import com.one.yjh.domain.entity.RefreshToken;
import com.one.yjh.domain.entity.Users;
import com.one.yjh.domain.repository.RefreshTokenRepository;
import com.one.yjh.domain.repository.UsersRepository;
import com.one.yjh.global.config.JwtProvider;

import com.one.yjh.global.exception.CustomException;
import com.one.yjh.global.exception.ErrorCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.one.yjh.global.exception.ErrorCode.*;

/**
 * 사용자의 로그인과 회원가입, 토큰 재발급을 담당하는 서비스 계층<br>
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
     * 로그인 시 새로운 액세스 토큰과 리프레시 토큰을 발급<br>
     *
     * @param request 클라이언트에서 입력한 아이디와 비밀번호를 담은 request 객체<br>
     * dto를 통해 받은 request 객체에서 이메일(아이디 대용)과 비밀 번호 정보를 활용해 <br>
     * 유저 존재 여부와 비밀 번호 일치 여부를 검사 <br>
     * JWT 원칙에 맞게 로그인 시마다 이미 받은 리프레시 토큰이 있다면 삭제 후 새로운 토큰 저장<br>
     * @throws CustomException 사용자를 찾을 수 없거나 비밀번호가 일치하지 않을 때<br>
     * @return 로그인 시 마다 기존의 refreshToken 교체
     *
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

        RefreshToken rToken = RefreshToken.builder()
                                .user(users)
                                .tokenValue(refreshToken)
                                .expiredAt(expiryDate)
                                .build();

        refreshTokenRepository.save(rToken);

        // DTO에 담아 반환
        return new TokenResponse(users.getId(), accessToken, refreshToken);
    }

    /**
     * request 객체를 통해 받은 새로운 유저 정보를 저장<br>
     *
     * @param request 클라이언트에서 입력받은 새로운 유저의 정보<br>
     * @throws CustomException 이미 존재하는 이메일인 경우 발생
     *
     */
    // 회원가입
    public void signup(SignupRequest request) {
        if (usersRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Users users = Users.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        usersRepository.save(users);
    }

    /**
     * 새로운 토큰을 재발급하여 dto로 전달 및 DB에 저장<br>
     *
     * @param request DB에 저장된 리프레시 토큰<br>
     * @return 새로운 accessToken과 refreshToken<br>
     * @throws CustomException 리프레시 토큰이 없거나(TOKEN_NOT_FOUND) 만료되었거나(EXPIRED_TOKEN) 유효하지 않을 때(INVALID_TOKEN) 발생
     */
    // 토큰 재발급
    @Transactional
    @Builder
    public TokenResponse reissue(ReissueRequest request) {
        // 리프레시 토큰의 존재 여부 확인
        RefreshToken token = refreshTokenRepository.findByTokenValue(request.refreshToken())
                .orElseThrow(() -> new CustomException(TOKEN_NOT_FOUND));

        // 요청받은 토큰과 DB의 토큰이 일치하는지
        if (!token.getTokenValue().equals(request.refreshToken())) {
            throw new CustomException(INVALID_TOKEN);
        }

        // 만료되었는지
        if (token.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new CustomException(EXPIRED_TOKEN);

        // 서명이 유효한지
        if(!jwtProvider.validateToken(token.getTokenValue()))
            throw new CustomException(INVALID_TOKEN);

        Users user = token.getUser();

        // 새로운 액세스 토큰과 리프레시 토큰 발급 및 저장, DTO에 전달
        String newAccessToken = jwtProvider.createAccessToken(user);
        String newRefreshToken = jwtProvider.createRefreshToken(user);
        token.updateTokenValue(newRefreshToken);

        return new TokenResponse(user.getId(), newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃하여 DB에서 토큰 정보를 삭제<br>
     *
     * @param request 사용자의 고유 식별 아이디<br>
     * @throws CustomException 이미 로그아웃을 한 경우 등 DB에 리프레시 토큰 정보가 없는데 또 삭제하려고 시도할 때 발생
     */
    @Transactional
    public void logout(LogoutRequest request) {

        if(!refreshTokenRepository.existsById(request.id()))
            throw new CustomException(USER_NOT_FOUND);

        refreshTokenRepository.deleteById(request.id());
    }
}
