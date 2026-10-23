package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ParameterNotExistsInExperimentException extends RuntimeException {

  public ParameterNotExistsInExperimentException(String name, UUID experiment_id) {
    super("Parameter " + name + " does not exist in experiment " + experiment_id);
  }

}
