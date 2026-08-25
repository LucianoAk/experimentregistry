package com.lucianoak.experimentregistry.dto.parameter.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateParameterResponseDTO(
    UUID id,
    String name,
    BigDecimal measurement,
    String unit,
    String description) {
}
