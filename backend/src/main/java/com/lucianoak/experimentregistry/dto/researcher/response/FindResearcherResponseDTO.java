package com.lucianoak.experimentregistry.dto.researcher.response;

import java.time.Instant;
import java.util.UUID;

public record FindResearcherResponseDTO(UUID id, String name, String email, Instant createdAt) {}

