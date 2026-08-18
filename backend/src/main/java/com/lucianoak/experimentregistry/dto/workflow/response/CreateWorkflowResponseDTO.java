package com.lucianoak.experimentregistry.dto.workflow.response;

import java.util.UUID;

public record CreateWorkflowResponseDTO(
    UUID id,
    Integer version 
) {}
