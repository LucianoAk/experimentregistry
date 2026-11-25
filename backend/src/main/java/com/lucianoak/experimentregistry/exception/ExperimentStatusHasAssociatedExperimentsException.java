package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class ExperimentStatusHasAssociatedExperimentsException extends RuntimeException {

  public ExperimentStatusHasAssociatedExperimentsException(UUID statusId) {
    super(
        "Operation cannot be completed, "
            + "there are still experiments associated with experiment status: "
            + statusId);
  }
}
