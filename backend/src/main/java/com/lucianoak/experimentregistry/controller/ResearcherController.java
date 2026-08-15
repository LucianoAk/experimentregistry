package com.lucianoak.experimentregistry.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.researcher.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.SearchResearcherResponseDTO;
import com.lucianoak.experimentregistry.service.ResearcherService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/researchers")
@RequiredArgsConstructor
class resourceNameController {

    private final ResearcherService researcherService;

    @GetMapping
    public ResponseEntity<List<SearchResearcherResponseDTO>> searchByName(
        @RequestParam
        @Size(min = 1, max = 255, message = "Name has invalid number of characters")
        String name
    ) {
        return ResponseEntity.ok(researcherService.findByName(name));
    }

    @GetMapping("{id}")
    public ResponseEntity<FindResearcherResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(researcherService.findById(id));

    }

    @PostMapping
    public ResponseEntity<CreateResearcherResponseDTO> create(@RequestBody @Valid CreateResearcherRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(researcherService.create(data));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable UUID id) {
        researcherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}