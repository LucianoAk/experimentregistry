package com.lucianoak.experimentregistry.dto.researcher.response;

import java.util.UUID;

public record ToggleResearcherActivationResponseDTO(
    UUID id,
    String name,
    String email,
    boolean active) {
}
