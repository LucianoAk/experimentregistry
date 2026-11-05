package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ParameterNotExistsInExperimentException extends RuntimeException {

  public ParameterNotExistsInExperimentException(UUID parameterId, UUID experimentId) {
    super("Parameter " + parameterId + " does not exist in experiment " + experimentId);
  }
}
