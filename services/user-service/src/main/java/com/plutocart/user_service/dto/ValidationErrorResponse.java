package com.plutocart.user_service.dto;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String message,
        List<FieldError> errors,
        Instant timestamp
) {
    public ValidationErrorResponse {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }


public record FieldError(
        String field,
        String message,
        Object rejectedValue
) {
}
}