package com.yostoya.innovoice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yostoya.innovoice.domain.HttpResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final var exceptionResponse = HttpResponse.builder()
                .timestamp(now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .status(FORBIDDEN)
                .statusCode(FORBIDDEN.value())
                .reason("Access denied. Invalid permissions")
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        final var outputStream = response.getOutputStream();
        new ObjectMapper().writeValue(outputStream, exceptionResponse);
        outputStream.flush();
    }
}
