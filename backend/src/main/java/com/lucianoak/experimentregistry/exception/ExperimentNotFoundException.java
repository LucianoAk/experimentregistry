package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ExperimentNotFoundException extends RuntimeException {

  public ExperimentNotFoundException(UUID id) {
    super("Experiment not found: " + id);
  }

}
