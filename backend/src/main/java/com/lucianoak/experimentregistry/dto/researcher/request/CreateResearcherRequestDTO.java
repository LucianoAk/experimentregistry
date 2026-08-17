package com.lucianoak.experimentregistry.dto.researcher.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResearcherRequestDTO(
    @NotBlank(message = "Field name is required")
    @Size(max = 255, message = "Max Researcher name is 255") 
    String name,
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email")
    String email

) {}
