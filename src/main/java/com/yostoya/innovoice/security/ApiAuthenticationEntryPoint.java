package com.yostoya.innovoice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yostoya.innovoice.domain.HttpResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final var responseBody = HttpResponse.builder()
                .timestamp(now().toString())
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .reason("You need to login first.")
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        final var outputStream = response.getOutputStream();
        new ObjectMapper().writeValue(outputStream, responseBody);
        outputStream.flush();
    }
}
