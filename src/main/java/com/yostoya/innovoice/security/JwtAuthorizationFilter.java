package com.yostoya.innovoice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.Map;

import static com.yostoya.innovoice.exception.ExceptionUtil.processError;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String EMAIL = "email";
    private static final String TOKEN = "token";
    private static final String[] PUBLIC_URLS = {
            "/user/login",
            "/user/register",
            "/user/verify/code"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) {
        try {

            //TODO: remove if not used anywhere else
            Map<String, String> requestValues = getRequestValues(request);
            String token = getToken(request);
            final String email = requestValues.get(EMAIL);

            if (tokenProvider.isTokenValid(email, token)) {

                var authorities = tokenProvider.getAuthorities(token);
                var authentication = tokenProvider.getAuthentication(email, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                log.error("Invalid token.");
                SecurityContextHolder.clearContext();
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            processError(response, ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        final String header = request.getHeader(AUTHORIZATION);

        return header == null ||
                !header.startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(OPTIONS.name()) ||
                Arrays.asList(PUBLIC_URLS).contains(request.getRequestURI());
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY)).get();
    }

    private Map<String, String> getRequestValues(HttpServletRequest request) {
        return Map.of(
                EMAIL, tokenProvider.getSubject(getToken(request), request),
                TOKEN, getToken(request)
        );
    }
}
