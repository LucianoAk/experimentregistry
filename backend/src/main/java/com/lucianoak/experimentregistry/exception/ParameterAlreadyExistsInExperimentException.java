package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ParameterAlreadyExistsInExperimentException extends RuntimeException {

  public ParameterAlreadyExistsInExperimentException(String name, UUID experiment_id) {
    super("Parameter " + name + " already exists in experiment " + experiment_id);
  }

}
