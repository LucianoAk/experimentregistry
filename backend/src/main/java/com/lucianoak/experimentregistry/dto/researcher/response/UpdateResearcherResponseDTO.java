package com.lucianoak.experimentregistry.dto.researcher.response;

import java.util.UUID;

public record UpdateResearcherResponseDTO(
    UUID id,
    String name,
    String email) {
}
