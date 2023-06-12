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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.http.ResponseEntity.ok;


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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));
        final var user = userService.getUserByEmail(loginDTO.email());
        return user.isUsingMfa() ? sendVerificationCode(user) : sendLoginResponse(user);
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verify2FACode(@PathVariable("email") String email,
                                                      @PathVariable("code") String code) {
        System.out.println("verifyAuthenticationCode");
        var user = userService.verify2FACode(email, code);
        return sendLoginResponse(user);
    }

    private ResponseEntity<HttpResponse> sendLoginResponse(UserDTO user) {
        return ok(HttpResponse.builder()
                .timestamp(now().toString())
                .data(Map.of(
                        "user", user,
                        "access_token", tokenProvider.createAccessToken(getUserPrinciple(user)),
                        "refresh_token", tokenProvider.createRefreshToken(getUserPrinciple(user)))
                )
                .status(OK)
                .statusCode(OK.value())
                .message("Logged in.")
                .build()
        );
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

    private static HttpResponse createdResponse(UserDTO result) {
        return HttpResponse.builder()
                .timestamp(now().toString())
                .data(Map.of("user", result))
                .status(CREATED)
                .statusCode(CREATED.value())
                .message("User created.")
                .build();
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        userService.sendVerificationCode(user);
        return ok(HttpResponse.builder()
                .timestamp(now().toString())
                .data(Map.of("user", user))
                .status(OK)
                .statusCode(OK.value())
                .message("Verification code sent.")
                .build());
    }
}
