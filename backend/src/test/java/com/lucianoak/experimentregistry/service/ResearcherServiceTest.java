package com.lucianoak.experimentregistry.service;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lucianoak.experimentregistry.dto.researcher.request.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.model.Researcher;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;

@ExtendWith(MockitoExtension.class)
class ResearcherServiceTest {

  @Mock
  private ResearcherRepository researcherRepository;

  @InjectMocks
  private ResearcherService researcherService;

  @Test
  void givenValidData_whenCreatingResearcher_thenResearcherIsCreated() {
    CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
        "John Doe",
        "john@example.com");

    UUID id = UUID.randomUUID();

    Researcher researcher = Researcher.builder()
        .name(dto.name())
        .email(dto.email())
        .build();

    researcher.setId(id);

    // when(researcherRepository.existsByEmail(dto.email());
    Mockito.when(researcherRepository.existsByEmail(dto.email())).thenReturn(false);
    Mockito.when(researcherRepository.save(Mockito.any(Researcher.class))).thenReturn(researcher);

    CreateResearcherResponseDTO response = researcherService.create(dto);

    Assertions.assertEquals(id, response.id());
    Assertions.assertEquals(dto.name(), response.name());
    Assertions.assertEquals(dto.email(), response.email());
    Mockito.verify(researcherRepository).save(Mockito.any(Researcher.class));
  }

}
