package com.lucianoak.experimentregistry.dto.experimentstatus.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateExperimentStatusesRequestDTO(
    @NotNull(message = "ID cannot be null") UUID id,
    @NotBlank(message = "Name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name) {}
