package com.example.oauthexample.domain.user.service;

import com.example.oauthexample.domain.user.dto.UserResponse;
import com.example.oauthexample.domain.role.entity.Permission;
import com.example.oauthexample.domain.user.entity.User;
import com.example.oauthexample.domain.user.repository.UserRepository;
import com.example.oauthexample.global.exception.ErrorCode;
import com.example.oauthexample.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public Set<Permission> getCurrentPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());
    }

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }
}
