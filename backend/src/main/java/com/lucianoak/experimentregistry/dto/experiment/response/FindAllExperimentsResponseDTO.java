package com.lucianoak.experimentregistry.dto.experiment.response;

import java.time.LocalDate;
import java.util.UUID;

public record FindAllExperimentsResponseDTO(
    UUID id,
    String title,
    LocalDate startDate,
    LocalDate finishDate,
    String status,
    String researcher) {
}
