package com.lucianoak.experimentregistry.dto.workflow.request;

import java.util.List;

import com.lucianoak.experimentregistry.dto.experimentstatus.request.CreateExperimentStatusRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateWorkflowRequestDTO(
    @NotEmpty(message = "At least one status is required")
    List<@Valid CreateExperimentStatusRequestDTO> statuses
) {}
