package jwd.practice.shopservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jwd.practice.shopservice.dto.response.ApiResponse;
import jwd.practice.shopservice.exception.ErrException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrException errorCode = ErrException.UNAUTHENTICATED;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
