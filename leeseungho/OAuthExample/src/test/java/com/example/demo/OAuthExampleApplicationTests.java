package com.example.demo;

import com.example.demo.domain.auth.dto.RefreshRequest;
import com.example.demo.domain.auth.entity.RefreshToken;
import com.example.demo.domain.auth.repository.RefreshTokenRepository;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.domain.auth.service.AuthService.GoogleUserInfo;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 구글 로그인 실습: 저장소를 모의 객체로 바꿔 HTTP 요청부터 JWT 인증까지 확인합니다.
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "jwt.secret=test-secret-only-used-for-local-jwt-tests",
        "jwt.access-token-expiration=600000",
        "jwt.refresh-token-expiration=3600000",
        "google.oauth.client-id=practice-client",
        "google.oauth.client-secret=practice-secret"
})
class OAuthExampleApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private PostRepository postRepository;

    @Test
    void googleCallbackAcceptsCodeFromDirectBrowserAuthorization() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        mvc.perform(get("/api/oauth2/callback/google"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_007"));
        mvc.perform(get("/api/oauth2/callback/google").param("error", "access_denied"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_007"));
        verify(authService, never()).authenticateWithGoogle(anyString());

        doReturn(new GoogleUserInfo("123456789", "learner@example.com", true))
                .when(authService).authenticateWithGoogle("practice-code");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(call -> {
            User user = call.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });
        MvcResult callback = mvc.perform(get("/api/oauth2/callback/google").param("code", "practice-code"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(savedUser.capture());
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser.getValue()));

        String accessToken = objectMapper.readTree(callback.getResponse().getContentAsString())
                .get("accessToken").stringValue();
        mvc.perform(get("/users/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("learner@example.com"));

    }

    @Test
    void passwordSignupLoginAndRefreshStillWork() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        String credentials = """
                {"email":"password@example.com","password":"practice-password"}
                """;
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isCreated());
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(savedUser.capture());
        User user = savedUser.getValue();
        ReflectionTestUtils.setField(user, "id", 2L);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        MvcResult login = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        ArgumentCaptor<RefreshToken> savedToken = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(savedToken.capture());
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(savedToken.getValue()));
        String refreshToken = objectMapper.readTree(login.getResponse().getContentAsString())
                .get("refreshToken").stringValue();
        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefreshRequest(refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

}
