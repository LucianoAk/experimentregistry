package com.lucianoak.experimentregistry.dto.workflow.response;

import java.time.Instant;
import java.util.UUID;

public record FindWorkflowResponseDTO(
    UUID id,
    Integer version,
    Instant createdAt
) {}
