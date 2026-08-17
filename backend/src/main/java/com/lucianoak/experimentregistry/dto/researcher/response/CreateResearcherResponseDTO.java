package com.lucianoak.experimentregistry.dto.researcher.response;

import java.util.UUID;

public record CreateResearcherResponseDTO(
    UUID id,
    String name,
    String email
) {}