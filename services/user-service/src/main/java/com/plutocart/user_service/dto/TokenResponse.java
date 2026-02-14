package com.plutocart.user_service.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn
) {
    public TokenResponse {
        if (tokenType == null || tokenType.isBlank()) {
            tokenType = "Bearer";
        }
    }
}
