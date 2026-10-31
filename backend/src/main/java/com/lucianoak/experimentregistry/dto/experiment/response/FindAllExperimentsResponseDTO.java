package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;

public record FindAllExperimentsResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    FindExperimentStatusResponseDTO status,
    FindResearcherResponseDTO researcher) {
}
