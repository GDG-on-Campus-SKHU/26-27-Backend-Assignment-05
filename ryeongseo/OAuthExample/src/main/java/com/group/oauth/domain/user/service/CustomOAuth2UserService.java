package com.group.oauth.domain.user.service;

import com.group.oauth.domain.user.entity.User;
import com.group.oauth.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");

        if (googleId == null || email == null) {
            throw new OAuth2AuthenticationException(
                    "구글 계정 정보를 가져올 수 없습니다."
            );
        }

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    if (userRepository.existsByEmail(email)) {
                        throw new OAuth2AuthenticationException(
                                "이미 가입된 이메일입니다."
                        );
                    }

                    return userRepository.save(
                            User.createGoogleUser(email, googleId)
                    );
                });

        return oauth2User;
    }
}
