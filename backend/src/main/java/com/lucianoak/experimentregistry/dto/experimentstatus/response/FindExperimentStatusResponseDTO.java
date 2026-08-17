package com.lucianoak.experimentregistry.dto.experimentstatus.response;

import java.util.UUID;

public record FindExperimentStatusResponseDTO(
    UUID id,
    String name,
    int sequenceOrder
) {}