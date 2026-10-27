package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class AlreadyInFirstStatusOfWorkflowException extends RuntimeException {

  public AlreadyInFirstStatusOfWorkflowException(UUID workflowId) {
    super("In first experiment status while trying to acess previous status of workflow: " + workflowId);
  }

}
