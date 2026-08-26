package com.lucianoak.experimentregistry.dto.experiment.response;

import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;

public record CreateExperimentResponseDTO(
    UUID id,
    String title,
    String workflow,
    String status,
    String researcher,
    List<CreateParameterResponseDTO> parameters) {
}
