package com.lucianoak.experimentregistry.dto.experiment.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExperimentRequestDTO(
    @NotNull @Size(max = 255) String title,
    @NotNull UUID workflowId,
    @NotNull UUID researcherId) {
}
