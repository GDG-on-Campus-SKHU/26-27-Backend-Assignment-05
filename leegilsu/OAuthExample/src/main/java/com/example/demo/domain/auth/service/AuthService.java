package com.example.demo.domain.auth.service;

import com.google.gson.annotations.SerializedName;
import com.example.demo.domain.auth.dto.LoginRequest;
import com.example.demo.domain.auth.dto.RefreshRequest;
import com.example.demo.domain.auth.dto.SignupRequest;
import com.example.demo.domain.auth.dto.TokenResponse;
import com.example.demo.domain.auth.entity.RefreshToken;
import com.example.demo.domain.auth.repository.RefreshTokenRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.ConflictException;
import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.UnauthorizedException;
import com.example.demo.global.jwt.JwtProvider;
import io.jsonwebtoken.JwtException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {

    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final RestClient restClient;

    @Autowired
    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
                       @Value("${google.oauth.client-id}") String clientId,
                       @Value("${google.oauth.client-secret}") String clientSecret,
                       @Value("${google.oauth.redirect-uri}") String redirectUri) {
        this(userRepository, refreshTokenRepository, passwordEncoder, jwtProvider,
                clientId, clientSecret, redirectUri, createRestClientBuilder());
    }

    AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
                String clientId, String clientSecret, String redirectUri, RestClient.Builder restClientBuilder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        // 구글 로그인 실습: 토큰과 UserInfo 응답을 Gson으로 읽습니다.
        this.restClient = restClientBuilder
                .configureMessageConverters(converters -> converters.withJsonConverter(new GsonHttpMessageConverter()))
                .build();
    }

    @Transactional
    public void signup(SignupRequest request) {
        String email = request.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL);
        }

        try {
            userRepository.saveAndFlush(new User(email, passwordEncoder.encode(request.password())));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL, e);
        }
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 확인
        // 구글 로그인 실습: 구글 전용 계정은 구글을 통해 로그인합니다.
        if (user.getGoogleId() != null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 토큰 발급
        return issueTokens(user.getId());
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Refresh Token 자체가 올바른지 확인 (서명, 만료, 종류)
        Long userId;
        try {
            userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. DB에 저장된 최신 Refresh Token과 같은지 확인
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        // 3. 새 토큰 발급 (이전 Refresh Token은 덮어써져서 더 이상 못 씀)
        return issueTokens(userId);
    }

    public URI googleAuthorizationUrl(String state) {
        requireConfigured();
        return UriComponentsBuilder.fromUriString(AUTHORIZATION_URL)
                .queryParam("response_type", "code")
                .queryParam("client_id", "{clientId}")
                .queryParam("redirect_uri", "{redirectUri}")
                .queryParam("scope", "{scope}")
                .queryParam("state", "{state}")
                .encode()
                .buildAndExpand(Map.of("clientId", clientId, "redirectUri", redirectUri,
                        "scope", "openid email profile", "state", state))
                .toUri();
    }

    // 구글 로그인 실습: 인가 코드를 교환한 후 구글 사용자 정보를 읽습니다.
    public GoogleUserInfo authenticateWithGoogle(String code) {
        requireConfigured();
        try {
            String googleAccessToken = exchangeCode(code);
            return getUserInfo(googleAccessToken);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 401) {
                throw new CustomException(ErrorCode.GOOGLE_LOGIN_FAILED);
            }
            throw new CustomException(ErrorCode.GOOGLE_API_UNAVAILABLE);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.GOOGLE_API_UNAVAILABLE);
        }
    }

    // 구글 로그인 실습: 구글 사용자와 우리 DB 계정을 연결하고 기존 JWT 발급을 재사용합니다.
    @Transactional
    public TokenResponse loginWithGoogle(String googleId, String email) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> createGoogleUser(googleId, email));
        return issueTokens(user.getId());
    }

    private User createGoogleUser(String googleId, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 기존 password의 NOT NULL 제약을 유지할 내부 값입니다.
        String passwordHash = passwordEncoder.encode(UUID.randomUUID().toString());
        try {
            return userRepository.saveAndFlush(User.withGoogleAccount(email, googleId, passwordHash));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(ErrorCode.DUPLICATE_EMAIL, e);
        }
    }

    private String exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        GoogleTokenResponse token = restClient.post().uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);
        if (token == null || !StringUtils.hasText(token.accessToken())) {
            throw new CustomException(ErrorCode.GOOGLE_API_UNAVAILABLE);
        }
        return token.accessToken();
    }

    // 구글 로그인 실습: 구글 리소스 서버의 UserInfo API에 액세스 토큰을 보냅니다.
    private GoogleUserInfo getUserInfo(String googleAccessToken) {
        GoogleUserInfo user = restClient.get().uri(USER_INFO_URL)
                .headers(headers -> headers.setBearerAuth(googleAccessToken))
                .retrieve()
                .body(GoogleUserInfo.class);
        if (user == null || !user.verifiedEmail()
                || !StringUtils.hasText(user.id()) || !StringUtils.hasText(user.email())
                || user.id().length() > 255 || user.email().length() > 255) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_FAILED);
        }
        return user;
    }

    private void requireConfigured() {
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)
                || !StringUtils.hasText(redirectUri)) {
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_NOT_CONFIGURED);
        }
    }

    private static RestClient.Builder createRestClientBuilder() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder().requestFactory(factory);
    }

    private TokenResponse issueTokens(Long userId) {
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // 처음 로그인이면 새로 저장하고, 이미 있으면 새 토큰으로 덮어씁니다.
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(userId, refreshToken));

        savedToken.updateToken(refreshToken);
        refreshTokenRepository.save(savedToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    private record GoogleTokenResponse(@SerializedName("access_token") String accessToken) {
    }

    public record GoogleUserInfo(String id, String email, @SerializedName("verified_email") boolean verifiedEmail) {
    }
}
