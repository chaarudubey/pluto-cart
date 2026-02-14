package com.plutocart.user_service.dto;

import java.time.Instant;

public record ErrorResponse(
    int status,
    String message,
    Instant timestamp,
    String error,
    String path
){
        public ErrorResponse {
            if (timestamp == null) {
                timestamp = Instant.now();
            }
        }
}
