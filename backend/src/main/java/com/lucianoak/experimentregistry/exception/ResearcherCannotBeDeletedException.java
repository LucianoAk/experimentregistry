package com.lucianoak.experimentregistry.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ResearcherCannotBeDeletedException extends RuntimeException {

  private final Map<String, String> errors = new LinkedHashMap<>();

  public ResearcherCannotBeDeletedException(UUID id) {
    super("Cannot delete researcher " + id);
  }

  public ResearcherCannotBeDeletedException addError(String field, String message) {
    this.errors.put(field, message);
    return this;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
