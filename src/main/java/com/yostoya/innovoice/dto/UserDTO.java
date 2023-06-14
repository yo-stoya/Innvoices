package com.yostoya.innovoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public record UserDTO(

         Long id,

         String firstName,

         String lastName,

         String email,

         String address,

         String phone,

         String title,

         String bio,

         String imageUrl,

         boolean enabled,

         Boolean isNotLocked,

         Boolean isUsingMfa,

         @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
         LocalDateTime createdOn,

         String role,

         String permissions
) {
}
