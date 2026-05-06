package com.one.yjh.domain.contoroller;

import com.one.yjh.domain.dto.UserResponse;
import com.one.yjh.domain.service.UserService;
import com.one.yjh.global.config.JwtProvider;
import com.one.yjh.global.exception.CustomException;
import com.one.yjh.global.exception.ErrorCode;
import com.one.yjh.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 비즈니스 로직의 엔드 포인트<br>
 * 로그인 된 사용자의 정보를 반환하는 API<br>
 * 서비스 계층과 분리하여 보안을 높임
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    /**
     * 현재 로그인한 사용자의 정보를 조회<br>
     *
     * @param bearerToken Authorization 헤더로 전달된 Jwt 토큰 정보<br>
     * @return 응답 메시지와 함께 사용자의 정보를 담음 response 객체<br>
     * @throws CustomException 토큰이 누락되었거나 유효하지 않은 토큰이 전달 될 때
     * */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            log.error("토큰이 존재하지 않음");
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String token = bearerToken.substring(7); // 헤더의 "Bearer " 제거
        if(!jwtProvider.validateToken(token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        Long userId = jwtProvider.getUserId(token); // 토큰을 기반으로 유저 아이디 추출

        UserResponse user = userService.getMyInfo(userId);
        return ApiResponse.success("요청에 성공하였습니다", user);

    }
}
