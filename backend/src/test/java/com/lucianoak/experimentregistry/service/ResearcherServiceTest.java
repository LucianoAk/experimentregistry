package com.lucianoak.experimentregistry.service;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    Mockito.when(researcherRepository.existsByEmail(dto.email())).thenReturn(false);
    Mockito.when(researcherRepository.save(Mockito.any(Researcher.class))).thenReturn(researcher);

    CreateResearcherResponseDTO result = researcherService.create(dto);

    Assertions.assertEquals(id, result.id());
    Assertions.assertEquals(dto.name(), result.name());
    Assertions.assertEquals(dto.email(), result.email());

    ArgumentCaptor<Researcher> captor = ArgumentCaptor.forClass(Researcher.class);
    Mockito.verify(researcherRepository).save(captor.capture());

    Researcher capturedResearcher = captor.getValue();

    Assertions.assertEquals(dto.name(), capturedResearcher.getName());
    Assertions.assertEquals(dto.email(), capturedResearcher.getEmail());
  }

}
