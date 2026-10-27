package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class LastStatusInWorkflowException extends RuntimeException {

  public LastStatusInWorkflowException(UUID workflowId) {
    super("In last experiment status while trying to acess next status of workflow: " + workflowId);
  }

}
