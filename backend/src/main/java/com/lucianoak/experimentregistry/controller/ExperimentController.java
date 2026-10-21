package com.lucianoak.experimentregistry.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.request.UpdateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindAllExperimentsResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindExperimentByIdResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.UpdateExperimentResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/experiments")
@RequiredArgsConstructor
public class ExperimentController {

  private final ExperimentService experimentService;

  // TODO: Create update and get routes

  @PostMapping
  public ResponseEntity<CreateExperimentResponseDTO> create(@RequestBody @Valid CreateExperimentRequestDTO dto) {
    return ResponseEntity.ok(experimentService.create(dto));
  }

  @GetMapping("/title-availability")
  public ResponseEntity<TitleAvailabilityResponseDTO> checkTittleAvailability(@RequestParam String title) {
    return ResponseEntity.ok(experimentService.checkTitleAvailability(title));
  }

  @GetMapping
  public ResponseEntity<List<FindAllExperimentsResponseDTO>> findAll() {
    return ResponseEntity.ok(experimentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<FindExperimentByIdResponseDTO> findById(@PathVariable @Valid UUID id) {
    return ResponseEntity.ok(experimentService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UpdateExperimentResponseDTO> update(@PathVariable @Valid UUID id,
      @RequestBody @Valid UpdateExperimentRequestDTO dto) {
    return ResponseEntity.ok(experimentService.update(id, dto));
  }

}
