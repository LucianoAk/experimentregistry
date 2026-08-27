package com.lucianoak.experimentregistry.service;

import java.util.List;
import java.util.Optional;
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
import com.lucianoak.experimentregistry.dto.researcher.response.EmailAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.SearchResearcherResponseDTO;
import com.lucianoak.experimentregistry.exception.EmailAlreadyExistsException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
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
  void givenResearchersFound_whenSearchingByName_thenReturnsResearchers() {
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
  }

  @Test
  void givenNoResearchersFound_whenSearchingByName_thenReturnEmptyList() {
    Mockito.when(researcherRepository.searchByName("Unknown")).thenReturn(List.<Researcher>of());

    List<SearchResearcherResponseDTO> result = researcherService.searchByName("Unknown");

    Assertions.assertTrue(result.isEmpty());

    Mockito.verify(researcherRepository).searchByName("Unknown");
  }

  @Test
  void givenExistingResearcher_whenFindingById_thenReturnResearcherResponse() {
    UUID id = UUID.randomUUID();

    Researcher researcher = Researcher.builder()
        .name("John Doe")
        .email("john@example.com")
        .build();
    researcher.setId(id);

    Mockito.when(researcherRepository.findById(id)).thenReturn(Optional.of(researcher));

    FindResearcherResponseDTO result = researcherService.findById(id);

    Assertions.assertEquals(researcher.getId(), result.id());
    Assertions.assertEquals(researcher.getName(), result.name());
    Assertions.assertEquals(researcher.getEmail(), result.email());

    Mockito.verify(researcherRepository).findById(id);
  }

  @Test
  void givenNonExistingResearcher_whenFindingById_thenThrowsResearcherNotFoundException() {
    UUID id = UUID.randomUUID();

    Mockito.when(researcherRepository.findById(id)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        ResearcherNotFoundException.class,
        () -> researcherService.findById(id));

    Mockito.verify(researcherRepository).findById(id);

  }

  @Test
  void givenExistingResearcher_whenDeleting_thenResearcherIsDeleted() {
    UUID id = UUID.randomUUID();
    Researcher researcher = Researcher.builder()
        .name("John Doe")
        .email("john@example.com")
        .build();
    researcher.setId(id);

    Mockito.when(researcherRepository.findById(id)).thenReturn(Optional.of(researcher));

    researcherService.delete(id);

    Mockito.verify(researcherRepository).findById(id);
    Mockito.verify(researcherRepository).delete(researcher);
  }

  @Test
  void givenNonExistingResearcher_whenDeleting_thenThrowsResearcherNotFoundException() {
    UUID id = UUID.randomUUID();
    Mockito.when(researcherRepository.findById(id)).thenReturn(Optional.empty());

    Assertions.assertThrows(
        ResearcherNotFoundException.class,
        () -> researcherService.delete(id));

    Mockito.verify(researcherRepository).findById(id);
    Mockito.verify(researcherRepository, Mockito.never()).delete(Mockito.any(Researcher.class));
  }

  @Test
  void givenAvailableEmail_whenCheckingEmailAvailability_thenReturnsAvailableTrue() {
    String email = "john@example.com";

    Mockito.when(researcherRepository.existsByEmail(email)).thenReturn(false);

    EmailAvailabilityResponseDTO result = researcherService.checkEmailAvailability(email);

    Assertions.assertTrue(result.available());
  }

  @Test
  void givenUnavailableEmail_whenCheckingEmailAvailability_thenReturnsAvailableFalse() {
    String email = "john@example.com";

    Mockito.when(researcherRepository.existsByEmail(email)).thenReturn(true);

    EmailAvailabilityResponseDTO result = researcherService.checkEmailAvailability(email);

    Assertions.assertFalse(result.available());
  }
}
