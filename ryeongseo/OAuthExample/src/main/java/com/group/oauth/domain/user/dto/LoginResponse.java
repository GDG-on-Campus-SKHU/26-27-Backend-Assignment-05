package com.group.oauth.domain.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
