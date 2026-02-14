package com.plutocart.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest (

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character(@$!%*?&#) ")
    String password,

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    String fullName,

    String phoneNumber
) {
    public RegistrationRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (fullName != null) {
            fullName = fullName.trim();
        }
    }
}

