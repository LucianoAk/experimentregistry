package com.lucianoak.experimentregistry.dto.experiment.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UpdateExperimentParametersRequestDTO(
    @Valid UUID id,
    @Size(max = 255) String name,
    BigDecimal measurement,
    @Size(max = 50) String unit,
    String description) {
}
