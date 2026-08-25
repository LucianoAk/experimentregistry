package com.lucianoak.experimentregistry.dto.experiment.response;

import java.util.UUID;

public record CreateExperimentResponseDTO(
    UUID id,
    String title,
    String workflow,
    String status,
    String researcher) {
}
