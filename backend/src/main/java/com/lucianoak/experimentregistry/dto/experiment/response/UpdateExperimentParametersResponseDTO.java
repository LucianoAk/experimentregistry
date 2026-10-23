package com.lucianoak.experimentregistry.dto.experiment.response;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateExperimentParametersResponseDTO(
    UUID id,
    String name,
    BigDecimal measurement,
    String unit,
    String description) {
}
