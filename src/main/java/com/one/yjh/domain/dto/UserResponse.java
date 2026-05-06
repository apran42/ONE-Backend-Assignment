package com.one.yjh.domain.dto;

import com.one.yjh.domain.entity.Users;

public record UserResponse(
        Long id,
        String email,
        String nickname
) {
    /**
     *
     * @param user 사용자 정보가 담긴 엔티티<br>
     * user 안에 있는 유저 식별용 아이디, 이메일, 이름(닉네임)을 반환<br>
     * @return 식별용 아이디, 이메일, 닉네임
     */
    public static UserResponse from(Users user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getNickname()
        );
    }
}
