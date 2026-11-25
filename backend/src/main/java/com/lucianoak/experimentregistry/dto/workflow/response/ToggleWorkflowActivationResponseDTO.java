package com.lucianoak.experimentregistry.dto.workflow.response;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import java.util.List;
import java.util.UUID;

public record ToggleWorkflowActivationResponseDTO(
    UUID id, Integer version, List<FindExperimentStatusResponseDTO> statues, boolean active) {}
