package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.UserResponse;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMyInfo(@AuthenticationPrincipal Long userId) {
        return userService.getMyInfo(userId);
    }
}
