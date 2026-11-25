package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class WorkflowHasAssociatedExperimentsException extends RuntimeException {

  public WorkflowHasAssociatedExperimentsException(UUID workflowId) {
    super(
        "Worflow cannot be delete, there are still experiments associated with it: " + workflowId);
  }
}
