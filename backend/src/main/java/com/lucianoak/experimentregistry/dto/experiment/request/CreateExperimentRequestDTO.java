package com.lucianoak.experimentregistry.dto.experiment.request;

import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExperimentRequestDTO(
    @NotNull @Size(max = 255) String title,
    @NotNull UUID workflowId,
    @NotNull UUID researcherId,
    @Valid List<CreateParameterRequestDTO> parameters) {
}
