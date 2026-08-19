package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class WorkflowNotFountException extends RuntimeException {

    public WorkflowNotFountException(UUID id) {
        super("Workflow not found: " + id);
    }
    
}
