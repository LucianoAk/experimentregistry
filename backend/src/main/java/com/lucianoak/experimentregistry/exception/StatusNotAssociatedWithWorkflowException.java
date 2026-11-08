package com.lucianoak.experimentregistry.exception;

import java.util.UUID;

public class StatusNotAssociatedWithWorkflowException extends RuntimeException {

  public StatusNotAssociatedWithWorkflowException(UUID statusId, UUID workflowId) {
    super("Experiment status '" + statusId + "' not associated with workflow '" + workflowId + "'");
  }
}
