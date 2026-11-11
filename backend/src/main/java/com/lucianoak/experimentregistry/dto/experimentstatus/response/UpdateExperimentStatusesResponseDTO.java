package com.lucianoak.experimentregistry.dto.experimentstatus.response;

import java.util.UUID;

public record UpdateExperimentStatusesResponseDTO(UUID id, String name, Integer sequenceOrder) {}
