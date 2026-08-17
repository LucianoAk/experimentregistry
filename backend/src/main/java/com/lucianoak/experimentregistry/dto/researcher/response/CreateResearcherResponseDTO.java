package com.lucianoak.experimentregistry.dto.researcher.response;

import java.time.Instant;
import java.util.UUID;

public record CreateResearcherResponseDTO(
    UUID id,
    String name,
    String email,
    Instant createAt
) {}