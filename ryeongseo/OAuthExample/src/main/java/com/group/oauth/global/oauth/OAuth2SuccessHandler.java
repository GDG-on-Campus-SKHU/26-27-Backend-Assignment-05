package com.group.oauth.global.oauth;


import com.group.oauth.domain.user.dto.LoginResponse;
import com.group.oauth.domain.user.entity.User;
import com.group.oauth.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final com.group.oauth.domain.user.repository.UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String googleId = oauth2User.getAttribute("sub");

        User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "구글 로그인 회원을 찾을 수 없습니다."
                        )
                );

        String accessToken =
                jwtProvider.createAccessToken(user.getId());

        String refreshToken =
                jwtProvider.createRefreshToken(user.getId());

        LoginResponse loginResponse =
                new LoginResponse(accessToken, refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        objectMapper.writeValue(
                response.getWriter(), loginResponse
        );
    }
}
