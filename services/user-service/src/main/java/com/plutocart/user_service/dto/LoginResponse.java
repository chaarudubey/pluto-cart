package com.plutocart.user_service.dto;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String username,
        String fullName,
        String accessToken,
        String refreshToken
) {
}
