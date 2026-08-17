package com.lucianoak.experimentregistry.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
        EmailAlreadyExistsException e
    ){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
    }

    @ExceptionHandler(ResearcherNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResearcherNotFound(
        ResearcherNotFoundException e
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e
    ) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(new ErrorResponse(status,
                "Invalid value for parameter '" + e.getName() + "'"
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(
                status,
                "Validation failed",
                errors
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception e
    ) {
        log.error("Unexpected error", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
            .status(status)
            .body(new ErrorResponse(
                status,
                "An unexpected error occurred"
            ));
    }
}
