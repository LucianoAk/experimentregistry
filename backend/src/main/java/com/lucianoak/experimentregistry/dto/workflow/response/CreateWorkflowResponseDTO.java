package com.lucianoak.experimentregistry.dto.workflow.response;

import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.CreateExperimentStatuesResponseDTO;

public record CreateWorkflowResponseDTO(
    UUID id,
    Integer version,
    List<CreateExperimentStatuesResponseDTO> experimentStatuses
) {}
