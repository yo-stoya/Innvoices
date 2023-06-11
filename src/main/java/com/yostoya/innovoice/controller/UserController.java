package com.yostoya.innovoice.controller;

import com.yostoya.innovoice.domain.HttpResponse;
import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.LoginDTO;
import com.yostoya.innovoice.dto.UserDTO;
import com.yostoya.innovoice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody @Valid User user) {
        final var created = userService.createUser(user);
        return ResponseEntity
                .created(mapUri(created.id()))
                .body(mapCreatedResponse(created));

    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));
        final var userDTO = userService.getUserByEmail(loginDTO.email());
        return ResponseEntity
                .ok(mapLoginResponse(userDTO));
    }

    private URI mapUri(Long id) {
        return URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/get/{id}")
                .build(id)
                .toString()
        );
    }

    private static HttpResponse mapCreatedResponse(UserDTO result) {
        return HttpResponse.builder()
                .timestamp(now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .data(Map.of("user", result))
                .status(CREATED)
                .statusCode(CREATED.value())
                .message("User created.")
                .build();
    }

    private static HttpResponse mapLoginResponse(UserDTO result) {
        return HttpResponse.builder()
                .timestamp(now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .data(Map.of("user", result))
                .status(OK)
                .statusCode(OK.value())
                .message("Logged in.")
                .build();
    }
}
