package com.lucianoak.experimentregistry.dto.researcher.response;

import java.util.UUID;

public record FindResearcherResponseDTO(
    UUID id,
    String name,
    String email
) {}