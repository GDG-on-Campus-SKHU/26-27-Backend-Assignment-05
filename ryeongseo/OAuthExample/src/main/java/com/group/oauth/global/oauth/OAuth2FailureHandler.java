package com.group.oauth.global.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        int status = HttpServletResponse.SC_UNAUTHORIZED;
        String code = "OAUTH_LOGIN_FAILED";
        String message = "구글 로그인에 실패했습니다.";
        if (request.getParameter("code") == null
                && request.getParameter("error") == null) {
            code = "OAUTH_CODE_MISSING";
            message = "구글 인가 코드가 누락되었습니다.";
        }
        if ("access_denied".equals(request.getParameter("error"))) {
            code = "OAUTH_LOGIN_CANCELLED";
            message = "구글 로그인이 취소되었습니다.";
        }

        if (hasEmailConflict(exception)) {
            status = HttpServletResponse.SC_CONFLICT;
            code = "EMAIL_ALREADY_EXISTS";
            message = "이미 가입된 이메일입니다.";
        }

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        String json = """
                {"code":"%s","message":"%s"}
                """.formatted(code, message);

        response.getWriter().write(json);
    }

    private boolean hasEmailConflict(Throwable exception) {
        Throwable cause = exception;

        while (cause != null) {
            if ("이미 가입된 이메일입니다.".equals(cause.getMessage())) {
                return true;
            }
            cause = cause.getCause();
        }

        return false;
    }
}
