package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class WorkflowHasAssociatedExperimentsException extends RuntimeException {

  public WorkflowHasAssociatedExperimentsException(UUID workflowId) {
    super(
        "Operation cannot be completed, there are still experiments associated with workflow: "
            + workflowId);
  }
}
