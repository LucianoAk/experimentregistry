package com.lucianoak.experimentregistry.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered in the system: " + email);
    }
}
