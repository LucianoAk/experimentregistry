package com.lucianoak.experimentregistry.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String error,
    String message,
    Instant timestamp,
    Map<String, String> errors
) {
    public ErrorResponse(HttpStatus status, String message) {
        this(
            status.value(), 
            status.getReasonPhrase(), 
            message, 
            Instant.now(),
            null
        );
    }

    public ErrorResponse(
        HttpStatus status, 
        String message,
        Map<String, String> errors
    ) {
        this(
            status.value(), 
            status.getReasonPhrase(), 
            message, 
            Instant.now(),
            errors
        );
    }
}
