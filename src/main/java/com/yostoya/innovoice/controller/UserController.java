package com.yostoya.innovoice.controller;

import com.yostoya.innovoice.domain.HttpResponse;
import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.LoginDTO;
import com.yostoya.innovoice.dto.UserDTO;
import com.yostoya.innovoice.dtomapper.UserMapper;
import com.yostoya.innovoice.security.TokenProvider;
import com.yostoya.innovoice.security.UserPrincipal;
import com.yostoya.innovoice.service.RoleService;
import com.yostoya.innovoice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid User user) {
        final var created = userService.createUser(user);
        return created(getCreadetUri(created.id()))
                .body(createdResponse(created));
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginDTO loginDTO) {
        final Authentication authentication = authenticate(loginDTO.email(), loginDTO.password());
        System.out.println(authentication);
        var user = getAuthenticatedUser(authentication);
        return user.isUsingMfa() ? ok(sendVerificationCode(user)) : ok(loginResponse(user));
    }

    private UserDTO getAuthenticatedUser(Authentication authentication) {
        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userMapper.toDTO(principal.getUser());
    }

    private Authentication authenticate(String email, String password) {
        final Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
        return authentication;
    }


    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verify2FACode(@PathVariable("email") String email,
                                                      @PathVariable("code") String code) {
        var user = userService.verify2FACode(email, code);
        return ok(loginResponse(user));
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> getProfile(Authentication authentication) {
        return ok(getHttpResponse(Map.of("user", authentication.getPrincipal()), OK, "Profile retrieved."));
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return new ResponseEntity<>(HttpResponse.builder()
                .timestamp(now().toString())
                .reason("No mapping found for " + request.getMethod() + " request on the server")
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .build(), NOT_FOUND
        );
    }

    private HttpResponse getHttpResponse(Map<?, ?> data, HttpStatus status, String message) {
        return HttpResponse.builder()
                .timestamp(now().toString())
                .data(data)
                .status(status)
                .statusCode(status.value())
                .message(message)
                .build();

    }

    private HttpResponse loginResponse(UserDTO user) {
        return getHttpResponse(Map.of(
                        "user", user,
                        "access_token", tokenProvider.createAccessToken(getUserPrinciple(user)),
                        "refresh_token", tokenProvider.createRefreshToken(getUserPrinciple(user))),
                OK, "Logged in.");
    }

    private UserPrincipal getUserPrinciple(UserDTO user) {
        return new UserPrincipal(
                userMapper.toUser(userService.getUserByEmail(user.email())),
                roleService.getRoleByUserId(user.id())
        );
    }

    private URI getCreadetUri(Long id) {
        return URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/get/{id}")
                .build(id)
                .toString()
        );
    }

    private HttpResponse createdResponse(UserDTO user) {
        return getHttpResponse(Map.of("user", user), CREATED, "User created.");
    }

    private HttpResponse sendVerificationCode(UserDTO user) {
        userService.sendVerificationCode(user);
        return getHttpResponse(Map.of("user", user), OK, "Verification code sent.");
    }

}
