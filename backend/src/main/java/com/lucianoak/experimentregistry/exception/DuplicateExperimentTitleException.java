package com.lucianoak.experimentregistry.exception;

public class DuplicateExperimentTitleException extends RuntimeException {

  public DuplicateExperimentTitleException(String name) {
    super("Experiment name: '" + name + "' already exists");
  }

}
