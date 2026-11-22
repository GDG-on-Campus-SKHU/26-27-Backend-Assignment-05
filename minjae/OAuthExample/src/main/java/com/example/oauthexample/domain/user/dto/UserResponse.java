package com.example.oauthexample.domain.user.dto;

import com.example.oauthexample.domain.role.dto.RoleResponse;
import com.example.oauthexample.domain.user.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String email,
        Set<RoleResponse> roles
) {
    public static UserResponse from(User user) {
        Set<RoleResponse> roles = user.getRoles().stream()
                .map(RoleResponse::from)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                roles
        );
    }
}
