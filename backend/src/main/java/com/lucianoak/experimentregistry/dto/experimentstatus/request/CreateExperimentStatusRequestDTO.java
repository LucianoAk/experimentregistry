package com.lucianoak.experimentregistry.dto.experimentstatus.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateExperimentStatusRequestDTO(
    @NotBlank(message = "Field name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    String name
) {}
