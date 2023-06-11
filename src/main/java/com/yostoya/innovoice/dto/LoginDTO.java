package com.yostoya.innovoice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO (

        @NotBlank(message = "Email cannot be empty")
        String email,

        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
