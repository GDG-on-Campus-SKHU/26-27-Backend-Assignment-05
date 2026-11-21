package com.group.oauth.domain.user.controller;

import com.group.oauth.domain.user.dto.UserResponse;
import com.group.oauth.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    public UserResponse getMyInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return userService.getMyInfo(userId);
    }
}
