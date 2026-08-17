package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ResearcherNotFoundException extends RuntimeException {
    public ResearcherNotFoundException(UUID id) {
        super("Researcher not found: " + id);
    }
}
