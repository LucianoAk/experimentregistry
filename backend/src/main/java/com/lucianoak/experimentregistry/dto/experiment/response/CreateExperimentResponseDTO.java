package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;

public record CreateExperimentResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    String workflow,
    String status,
    String researcher,
    List<CreateParameterResponseDTO> parameters) {
}
