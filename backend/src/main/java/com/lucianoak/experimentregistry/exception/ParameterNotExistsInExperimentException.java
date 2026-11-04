package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ParameterNotExistsInExperimentException extends RuntimeException {

  public ParameterNotExistsInExperimentException(String name, UUID experimentId) {
    super("Parameter " + name + " does not exist in experiment " + experimentId);
  }
}
