package com.lucianoak.experimentregistry.service;

import java.util.ArrayList;
import java.util.List;
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
import com.lucianoak.experimentregistry.dto.researcher.response.SearchResearcherResponseDTO;
import com.lucianoak.experimentregistry.exception.EmailAlreadyExistsException;
import com.lucianoak.experimentregistry.model.Researcher;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;

@ExtendWith(MockitoExtension.class)
class ResearcherServiceTest {

  @Mock
  private ResearcherRepository researcherRepository;

  @InjectMocks
  private ResearcherService researcherService;

  @Test
  void givenValidData_whenCreatingResearcher_thenSavesResearcherAndReturnsResponse() {
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

  @Test
  void givenExistingEmail_whenCreatingResearcher_thenThrowsEmailAlreadyExistsException() {
    CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
        "John Doe",
        "john@example.com");

    Mockito.when(researcherRepository.existsByEmail(dto.email())).thenReturn(true);

    Assertions.assertThrows(
        EmailAlreadyExistsException.class,
        () -> researcherService.create(dto));

    Mockito.verify(researcherRepository, Mockito.never()).save(Mockito.any(Researcher.class));
  }

  @Test
  void givenReseachersFound_whenSearchingByName_thenReturnsResearchers() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Researcher researcher1 = Researcher.builder()
        .name("John Doe")
        .email("john@example.com")
        .build();
    researcher1.setId(id1);
    Researcher researcher2 = Researcher.builder()
        .name("Jane Doe")
        .email("jane@example.com")
        .build();
    researcher2.setId(id2);

    Mockito.when(researcherRepository.searchByName("Doe")).thenReturn(List.of(researcher1, researcher2));

    List<SearchResearcherResponseDTO> expected = List.of(
        new SearchResearcherResponseDTO(researcher1.getId(), researcher1.getName(), researcher1.getEmail()),
        new SearchResearcherResponseDTO(researcher2.getId(), researcher2.getName(), researcher2.getEmail()));

    List<SearchResearcherResponseDTO> result = researcherService.searchByName("Doe");

    Assertions.assertEquals(expected, result);

    Mockito.verify(researcherRepository).searchByName("Doe");
  }
}
