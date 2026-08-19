package com.lucianoak.experimentregistry.dto.workflow.response;

import java.time.Instant;
import java.util.UUID;

public record FindAllWorkflowResponseDTO(
    UUID id,
    Integer version,
    Instant createdAt
) {}
