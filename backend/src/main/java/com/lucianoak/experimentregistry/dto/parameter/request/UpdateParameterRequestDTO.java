package com.lucianoak.experimentregistry.dto.parameter.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Size;

public record UpdateParameterRequestDTO(
    UUID id,
    @Size(max = 255) String name,
    BigDecimal measurement,
    @Size(max = 50) String unit,
    String description) {
}
