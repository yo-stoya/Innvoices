package com.yostoya.innovoice.security;

import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.repository.UserRepository;
import com.yostoya.innovoice.repository.impl.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiAccessDeniedHandler deniedHandler;
    private final ApiAuthenticationEntryPoint entryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private static final String[] PUBLIC_URLS = {
            "/user/login/**",
            "/user/register/**",
            "/user/verify/code/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .exceptionHandling(exception -> exception.accessDeniedHandler(deniedHandler).authenticationEntryPoint(entryPoint));

        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(PUBLIC_URLS).permitAll();
            authorize.requestMatchers(DELETE, "/users/delete/**").hasAuthority("DELETE:USER");
            authorize.requestMatchers(DELETE, "/customer/delete/**").hasAuthority("DELETE:CUSTOMER");
            authorize.anyRequest().authenticated();
        });

        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        final var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

}
