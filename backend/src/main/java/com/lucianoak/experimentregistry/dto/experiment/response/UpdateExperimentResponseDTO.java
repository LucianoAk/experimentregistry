package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;

public record UpdateExperimentResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    FindWorkflowResponseDTO workflow,
    FindExperimentStatusResponseDTO status,
    FindResearcherResponseDTO researcher) {
}
