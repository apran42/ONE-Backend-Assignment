package com.one.yjh.global.config;

import com.one.yjh.domain.entity.Users;
import com.one.yjh.global.exception.CustomException;
import com.one.yjh.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Key key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // AccessToken 생성
    public String createAccessToken(Users user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration * 1000);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(Users user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 유저의 ID 반환
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰입니다.");
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않은 형식의 토큰입니다");
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
            throw new CustomException(ErrorCode.EMPTY_TOKEN);
        }
    }

    // 토큰 내의 정보를 추출
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();
    }
}
