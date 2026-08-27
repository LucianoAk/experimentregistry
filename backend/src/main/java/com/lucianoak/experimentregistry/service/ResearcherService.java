package com.lucianoak.experimentregistry.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.researcher.request.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.EmailAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.SearchResearcherResponseDTO;
import com.lucianoak.experimentregistry.exception.EmailAlreadyExistsException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.model.Researcher;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResearcherService {

  private final ResearcherRepository researcherRepository;

  @Transactional
  public CreateResearcherResponseDTO create(CreateResearcherRequestDTO dto) {
    if (researcherRepository.existsByEmail(dto.email())) {
      throw new EmailAlreadyExistsException(dto.email());
    }

    Researcher savedResearcher = researcherRepository.save(Researcher.builder()
        .name(dto.name())
        .email(dto.email())
        .build());

    return new CreateResearcherResponseDTO(
        savedResearcher.getId(),
        savedResearcher.getName(),
        savedResearcher.getEmail());
  }

  public List<SearchResearcherResponseDTO> searchByName(String name) {
    return researcherRepository.searchByName(name).stream()
        .map(r -> new SearchResearcherResponseDTO(
            r.getId(),
            r.getName(),
            r.getEmail()))
        .toList();
  }

  public FindResearcherResponseDTO findById(UUID id) {
    Researcher researcher = researcherRepository.findById(id)
        .orElseThrow(() -> new ResearcherNotFoundException(id));

    return new FindResearcherResponseDTO(
        researcher.getId(),
        researcher.getName(),
        researcher.getEmail());
  }

  @Transactional
  public void delete(UUID id) {
    Researcher researcher = researcherRepository.findById(id)
        .orElseThrow(() -> new ResearcherNotFoundException(id));
    researcherRepository.delete(researcher);
  }

  public EmailAvailabilityResponseDTO checkEmailAvailability(String email) {
    return new EmailAvailabilityResponseDTO(
        !researcherRepository.existsByEmail(email));
  }
}
