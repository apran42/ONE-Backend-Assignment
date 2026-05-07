package com.one.yjh.global.config;

import com.one.yjh.global.exception.CustomException;
import com.one.yjh.global.exception.ErrorCode;
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
 * 모든 HTTP 요청마다 Jwt 토큰의 유효성을 검사하고 인증 정보를 저장하는 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    /**
     * 요청 헤더에서 jwt를 추출하고, 유효하면 인증 정보를 등록<br>
     *
     * @param request http 요청 객체<br>
     * @param response http 응답 객체<br>
     * @param filterChain 다음 필터로의 흐름 제어
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 인증 헤더에서 토큰을 추출
            String bearerToken = request.getHeader("Authorization");
            String token = null;

            // 토큰이 있으면 "Bearer "을 제거
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
                token = bearerToken.substring(7);
            // 토큰이 없으면
            if (token == null)
                request.setAttribute("exception", ErrorCode.TOKEN_NOT_FOUND);
            else if (jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.getUserId(token);
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException e){
            request.setAttribute("exception", e.getErrorCode());
        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
        }
        filterChain.doFilter(request, response);
    }
}