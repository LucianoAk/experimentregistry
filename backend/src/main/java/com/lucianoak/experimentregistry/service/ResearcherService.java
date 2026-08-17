package com.lucianoak.experimentregistry.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lucianoak.experimentregistry.dto.researcher.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.SearchResearcherResponseDTO;
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

        Researcher savedResearcher = researcherRepository.save(Researcher.builder()
                .name(dto.name())
                .email(dto.email())
                .build());

        return new CreateResearcherResponseDTO(
            savedResearcher.getId(), 
            savedResearcher.getName(),
            savedResearcher.getEmail(),
            savedResearcher.getCreatedAt()
        );
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
        Researcher researcher = researcherRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Researcher not found"));
        
        return new FindResearcherResponseDTO(
            researcher.getId(),
            researcher.getName(),
            researcher.getEmail()
        );
    }

    @Transactional
    public void delete(UUID id) {
        Researcher researcher = researcherRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Researcher not found"));
        researcherRepository.delete(researcher);
    }

    public boolean existsByEmail(String email) {
        return researcherRepository.existsByEmail(email);
    }
}