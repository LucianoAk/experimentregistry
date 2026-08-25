package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ParameterNotFoundException extends RuntimeException {

  public ParameterNotFoundException(UUID id) {
    super("Cannot find parameter" + id);
  }

}
