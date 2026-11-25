package com.example.demo.domain.auth.controller;

import com.example.demo.domain.auth.dto.LoginRequest;
import com.example.demo.domain.auth.dto.RefreshRequest;
import com.example.demo.domain.auth.dto.SignupRequest;
import com.example.demo.domain.auth.dto.TokenResponse;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.domain.auth.service.AuthService.GoogleUserInfo;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final String GOOGLE_STATE_KEY = "googleOAuthState";

    private final AuthService authService;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/auth/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/api/oauth2/login/google")
    public ResponseEntity<Void> startGoogleLogin(HttpServletRequest request) {
        String state = UUID.randomUUID().toString();
        URI authorizationUrl = authService.googleAuthorizationUrl(state);
        request.getSession().setAttribute(GOOGLE_STATE_KEY, new GoogleLoginState(state));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(authorizationUrl)
                .cacheControl(CacheControl.noStore())
                .build();
    }

    @GetMapping("/api/oauth2/callback/google")
    public ResponseEntity<TokenResponse> googleCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null
                || !(session.getAttribute(GOOGLE_STATE_KEY) instanceof GoogleLoginState pending)
                || !pending.consume(state)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (error != null || !StringUtils.hasText(code)) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_FAILED);
        }

        GoogleUserInfo googleUser = authService.authenticateWithGoogle(code);
        TokenResponse tokens = authService.loginWithGoogle(googleUser.id(), googleUser.email());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer").body(tokens);
    }

    private record GoogleLoginState(AtomicReference<String> value) {
        private GoogleLoginState(String state) {
            this(new AtomicReference<>(state));
        }

        private boolean consume(String returnedState) {
            String expectedState = value.get();
            return expectedState != null && expectedState.equals(returnedState)
                    && value.compareAndSet(expectedState, null);
        }
    }
}
