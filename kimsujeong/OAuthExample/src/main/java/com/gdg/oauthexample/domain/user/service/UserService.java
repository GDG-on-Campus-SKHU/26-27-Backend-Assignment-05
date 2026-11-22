package com.gdg.oauthexample.domain.user.service;


import com.gdg.oauthexample.domain.user.dto.UserResponse;
import com.gdg.oauthexample.domain.user.entity.User;
import com.gdg.oauthexample.domain.user.repository.UserRepository;
import com.gdg.oauthexample.global.exception.ErrorCode;
import com.gdg.oauthexample.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }
}
