package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

// REFACTOR: unify this exception with 'ParameterNotExistsInExperimentException'
public class ParameterNotFoundException extends RuntimeException {

  public ParameterNotFoundException(UUID id) {
    super("Cannot find parameter" + id);
  }
}
