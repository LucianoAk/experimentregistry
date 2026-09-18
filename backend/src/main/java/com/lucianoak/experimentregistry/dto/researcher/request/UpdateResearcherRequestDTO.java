package com.lucianoak.experimentregistry.dto.researcher.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateResearcherRequestDTO(
    @Size(max = 255, message = "Name must not exceed 255 characters") String name,
    @Email(message = "Invalid email") String email) {
}
