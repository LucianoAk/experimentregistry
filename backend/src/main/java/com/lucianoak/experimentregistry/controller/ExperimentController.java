package com.lucianoak.experimentregistry.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/experiment")
@RequiredArgsConstructor
public class ExperimentController {

  private final ExperimentService experimentService;

  @PostMapping
  public ResponseEntity<CreateExperimentResponseDTO> create(@RequestBody @Valid CreateExperimentRequestDTO dto) {
    return ResponseEntity.ok(experimentService.create(dto));
  }

  @GetMapping
  public ResponseEntity<TitleAvailabilityResponseDTO> checkTittleAvailability(@RequestParam String title) {
    return ResponseEntity.ok(experimentService.checkTitleAvailability(title));
  }

}
