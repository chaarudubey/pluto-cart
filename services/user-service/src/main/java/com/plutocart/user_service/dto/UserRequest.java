package com.plutocart.user_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @Size(max = 150, message = "Full name must not exceed 150 characters")
    String fullName,

    @Size(max = 10, message = "Phone number should be 10 characters")
    @Size(min = 10, message = "Phone number should be 10 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    String phoneNumber,

    Boolean isActive
) {
    public UserRequest {
        if (fullName != null) {
            fullName = fullName.trim();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}

