package com.lucianoak.experimentregistry.dto.workflow.response;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.CreateExperimentStatuesResponseDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateWorkflowResponseDTO(
    UUID id,
    Integer version,
    List<CreateExperimentStatuesResponseDTO> statuses,
    Instant createdAt) {}
