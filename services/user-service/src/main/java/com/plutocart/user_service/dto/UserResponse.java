package com.plutocart.user_service.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String phoneNumber,
        Boolean isActive,
        String userType,
        Instant createdAt,
        Instant updatedAt,
        Instant lastLoginAt
) {
}
