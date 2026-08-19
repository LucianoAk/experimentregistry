package com.lucianoak.experimentregistry.dto.experimentstatus.response;

import java.util.UUID;

public record CreateExperimentStatuesResponseDTO(
    UUID id,
    String name,
    Integer sequenceOrder
) {}
