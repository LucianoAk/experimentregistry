package com.lucianoak.experimentregistry.dto.experiment.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.lucianoak.experimentregistry.dto.parameter.request.UpdateParameterRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UpdateExperimentRequestDTO(
    @Size(max = 255) String title,
    LocalDate startDate,
    LocalDate finishDate,
    String result,
    UUID workflow,
    UUID researcher,
    @NotEmpty @Valid List<UpdateParameterRequestDTO> parameters) {
}
