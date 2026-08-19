package com.lucianoak.experimentregistry.exception;

public class DuplicateStatusException extends RuntimeException {

    public DuplicateStatusException(String status) {
        super("Status '" + status + "' is duplicate in workflow");
    }

}
