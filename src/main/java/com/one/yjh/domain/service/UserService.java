package com.one.yjh.domain.service;

import com.one.yjh.domain.dto.UserResponse;
import com.one.yjh.domain.entity.Users;
import com.one.yjh.domain.repository.UsersRepository;
import com.one.yjh.global.exception.CustomException;
import com.one.yjh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;

    /**
     * 로그인 된 사용자의 정보를 반환하는 서비스 계층 <br>
     * DTO를 통해 필요한 값만 반환
     *
     * @param userId 조회할 사용자의 고유 식별 id
     * @return entity에서 변환된 사용자 정보 DTO
     * @throws CustomException userId의 사용자가 존재하지 않을 때 발생
     */
    public UserResponse getMyInfo(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }
}
