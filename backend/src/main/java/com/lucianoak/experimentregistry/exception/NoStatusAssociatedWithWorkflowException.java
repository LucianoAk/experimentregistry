package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class NoStatusAssociatedWithWorkflowException extends RuntimeException {

  public NoStatusAssociatedWithWorkflowException(UUID workflowId) {
    super("There are no experiment status associated with the workflow: " + workflowId);
  }

}
