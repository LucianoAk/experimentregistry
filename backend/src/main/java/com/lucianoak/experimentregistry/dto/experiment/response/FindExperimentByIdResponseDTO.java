package com.lucianoak.experimentregistry.dto.experiment.response;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.FindParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FindExperimentByIdResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    FindWorkflowResponseDTO workflow,
    FindExperimentStatusResponseDTO status,
    FindResearcherResponseDTO researcher,
    List<FindParameterResponseDTO> parameters,
    Instant createdAt) {}
