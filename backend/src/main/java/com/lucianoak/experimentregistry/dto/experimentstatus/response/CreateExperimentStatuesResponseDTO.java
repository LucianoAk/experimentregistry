package com.lucianoak.experimentregistry.dto.experimentstatus.response;

import java.time.Instant;
import java.util.UUID;

public record CreateExperimentStatuesResponseDTO(
    UUID id, String name, Integer sequenceOrder, Instant createdAt) {}
