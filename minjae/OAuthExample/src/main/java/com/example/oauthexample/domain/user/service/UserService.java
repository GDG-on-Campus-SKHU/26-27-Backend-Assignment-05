package com.example.oauthexample.domain.user.service;

import com.example.oauthexample.domain.user.dto.UserResponse;
import com.example.oauthexample.domain.user.entity.User;
import com.example.oauthexample.domain.user.repository.UserRepository;
import com.example.oauthexample.global.exception.ErrorCode;
import com.example.oauthexample.global.exception.NotFoundException;

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
