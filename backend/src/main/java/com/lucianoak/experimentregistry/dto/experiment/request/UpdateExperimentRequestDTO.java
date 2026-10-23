package com.lucianoak.experimentregistry.dto.experiment.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Size;

public record UpdateExperimentRequestDTO(
    @Size(max = 255) String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    UUID workflowId,
    UUID researcherId) {
}
