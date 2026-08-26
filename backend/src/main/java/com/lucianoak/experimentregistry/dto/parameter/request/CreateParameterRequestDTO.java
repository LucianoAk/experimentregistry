package com.lucianoak.experimentregistry.dto.parameter.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateParameterRequestDTO(
    @Size(max = 255) String name,
    @NotNull @Digits(integer = 13, fraction = 6) BigDecimal measurament,
    @Size(max = 50) String unit,
    String description) {
}
