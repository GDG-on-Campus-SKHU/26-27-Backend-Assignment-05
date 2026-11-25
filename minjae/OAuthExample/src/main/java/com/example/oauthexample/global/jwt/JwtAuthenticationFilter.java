package com.example.oauthexample.global.jwt;

import com.example.oauthexample.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 꺼내기
        String token = resolveToken(request);

        if (token != null) {
            try {

                Claims claims = jwtProvider.getClaimsFromAccessToken(token);

                Long userId = Long.valueOf(claims.getSubject());

                // 토큰 발급 당시의 권한 대신 DB의 현재 권한을 사용합니다.
                List<SimpleGrantedAuthority> authorities =
                        userService.getCurrentPermissions(userId).stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                                .toList();

                Authentication authentication =
                        UsernamePasswordAuthenticationToken.authenticated(
                                userId,
                                null,
                                authorities
                        );

                // 4. SecurityContext에 저장 → 이제 Spring Security가 "인증된 요청"으로 봅니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // 잘못된 토큰(위조, 만료 등)이면 인증 정보를 만들지 않고 그냥 넘어갑니다.
                // → 보호된 API라면 뒤에서 Spring Security가 401을 응답합니다.
            }
        }

        // 5. 다음 필터로 넘기기 (토큰이 없거나 잘못됐으면 인증 정보 없이 넘어갑니다)
        filterChain.doFilter(request, response);
    }

    // "Authorization: Bearer xxxxx" 에서 xxxxx 부분만 꺼냅니다.
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " (공백 포함 7글자) 이후
        }
        return null;
    }
}
