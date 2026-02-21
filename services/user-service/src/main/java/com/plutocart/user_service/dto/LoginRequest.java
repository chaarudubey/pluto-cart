package com.plutocart.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required and is an email address")
        @Size(max = 255, message = "Username must not exceed 255 characters")
        String username,

        @NotBlank(message = "Password is required")
        String password) {

    public LoginRequest {
        if (username != null) {
            username = username.trim().toLowerCase();
        }
    }
}
