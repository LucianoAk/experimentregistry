package com.lucianoak.experimentregistry.dto.workflow.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;

public record FindWorkflowResponseDTO(
    UUID id,
    Integer version,
    List<FindExperimentStatusResponseDTO> statues,
    Instant createdAt
) {}
