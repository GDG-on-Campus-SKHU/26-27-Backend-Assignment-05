package com.example.oauthexample.domain.auth.controller;

import com.example.oauthexample.domain.auth.service.AuthService;
import com.example.oauthexample.domain.auth.service.AuthService.GoogleUserInfo;
import com.example.oauthexample.domain.auth.dto.LoginRequest;
import com.example.oauthexample.domain.auth.dto.RefreshRequest;
import com.example.oauthexample.domain.auth.dto.SignupRequest;
import com.example.oauthexample.domain.auth.dto.TokenResponse;
import com.example.oauthexample.global.exception.CustomException;
import com.example.oauthexample.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final String GOOGLE_STATE_KEY = "googleOAuthState";

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
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
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer")
                .body(tokens);
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

