package com.lucianoak.experimentregistry.dto.researcher;

import java.util.UUID;

public record SearchResearcherResponseDTO(
    UUID id,
    String name,
    String email
) {}