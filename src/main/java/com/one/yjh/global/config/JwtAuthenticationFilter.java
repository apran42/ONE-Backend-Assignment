package com.one.yjh.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 모든 HTTP 요청마다 Jwt 토큰의 유효성을 검사하는 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    /**
     * 요청 헤더에서 jwt를 추출하고, 유효하면 인증 정보를 등록
     *
     * @param request http 요청 객체
     * @param response http 응답 객체
     * @param filterChain 다음 필터로의 흐름 제어
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 인증 헤더에서 토큰을 추출
        String bearerToken = request.getHeader("Authorization");
        String token = null;

        // 토큰이 있으면 "Bearer "을 제거
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            token = bearerToken.substring(7);

        // 토큰이 존재하고 유효성 검사를 통과하면
        if (token != null && jwtProvider.validateToken(token)) {
            Long userId = jwtProvider.getUserId(token);
            // 인증할 수 있는 객체를 생성
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            // 인증할 수 있도록
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
