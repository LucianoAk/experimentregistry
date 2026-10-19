package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.FindParameterResponseDTO;

public record FindExperimentByIdResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    UUID workflow,
    FindExperimentStatusResponseDTO status,
    UUID researcher,
    List<FindParameterResponseDTO> parameters) {
}
