package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;

public record CreateExperimentResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    FindWorkflowResponseDTO workflow,
    FindExperimentStatusResponseDTO status,
    FindResearcherResponseDTO researcher,
    List<CreateParameterResponseDTO> parameters) {
}
