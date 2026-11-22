package com.example.demo.domain.auth.service;

import com.example.demo.domain.auth.repository.RefreshTokenRepository;
import com.example.demo.domain.auth.service.AuthService.GoogleUserInfo;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

// 구글 로그인 실습: 토큰 요청과 UserInfo 요청에 담는 값을 확인합니다.
class AuthServiceGoogleTest {

    @Test
    void sendsCodeAndClientSecretThenReadsUserInfoWithGoogleAccessToken() {
        String callback = "http://localhost:8080/api/oauth2/callback/google";
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AuthService service = new AuthService(mock(UserRepository.class), mock(RefreshTokenRepository.class),
                mock(PasswordEncoder.class), mock(JwtProvider.class),
                "practice-client", "practice-secret", callback, builder);

        LinkedMultiValueMap<String, String> tokenForm = new LinkedMultiValueMap<>();
        tokenForm.add("code", "code+with/symbols");
        tokenForm.add("client_id", "practice-client");
        tokenForm.add("client_secret", "practice-secret");
        tokenForm.add("redirect_uri", callback);
        tokenForm.add("grant_type", "authorization_code");

        server.expect(requestTo("https://oauth2.googleapis.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().formData(tokenForm))
                .andRespond(withSuccess("""
                        {"access_token":"google-access-token","token_type":"Bearer"}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://www.googleapis.com/oauth2/v2/userinfo"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer google-access-token"))
                .andRespond(withSuccess("""
                        {"id":"123456789","email":"learner@example.com","verified_email":true,"name":"Learner"}
                        """, MediaType.APPLICATION_JSON));

        assertThat(service.authenticateWithGoogle("code+with/symbols"))
                .isEqualTo(new GoogleUserInfo("123456789", "learner@example.com", true));
        server.verify();
    }
}
