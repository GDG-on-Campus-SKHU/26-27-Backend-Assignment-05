package com.group.oauth.domain.user.service;

import com.group.oauth.domain.user.dto.LoginRequest;
import com.group.oauth.domain.user.dto.LoginResponse;
import com.group.oauth.domain.user.dto.SignupRequest;
import com.group.oauth.domain.user.dto.UserResponse;
import com.group.oauth.domain.user.entity.User;
import com.group.oauth.domain.user.repository.UserRepository;
import com.group.oauth.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 가입된 이메일입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(request.email(), encodedPassword);

        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "이메일 또는 비밀번호가 틀렸습니다."
                ));

        if (user.getPassword() == null ||
                !passwordEncoder.matches(
                        request.password(), user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 틀렸습니다."
            );
        }

        String accessToken =
                jwtProvider.createAccessToken(user.getId());

        String refreshToken =
                jwtProvider.createRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "회원을 찾을 수 없습니다."
                ));

        return new UserResponse(user.getId(), user.getEmail());
    }
}
