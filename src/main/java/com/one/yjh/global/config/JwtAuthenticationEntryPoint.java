package com.one.yjh.global.config;

import com.one.yjh.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");
        if (errorCode == null) {
            errorCode = ErrorCode.INVALID_TOKEN;
        }

        setResponse(response, errorCode);
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // ApiResponse 공통 규격에 맞춘 JSON 응답
        String json = String.format(
                "{\"success\":false, \"message\":\"%s\", \"data\":null}",
                errorCode.getMessage()
        );

        response.getWriter().print(json);
    }
}
