package com.lucianoak.experimentregistry.dto.researcher.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResearcherRequestDTO(
    @NotBlank(message = "Field name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email

) {}
