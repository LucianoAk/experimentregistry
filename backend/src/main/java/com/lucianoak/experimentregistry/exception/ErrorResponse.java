package com.lucianoak.experimentregistry.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    int status,
    String error,
    String message,
    Instant timestamp
) {
    public ErrorResponse(HttpStatus status, String message) {
        this(
            status.value(), 
            status.getReasonPhrase(), 
            message, 
            Instant.now()
        );
    }
}
