package com.lucianoak.experimentregistry.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.request.UpdateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.AdvanceExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindAllExperimentsResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindExperimentByIdResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.UpdateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.UpdateExperimentParametersRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.UpdateExperimentParametersResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/experiments")
@RequiredArgsConstructor
public class ExperimentController {

  private final ExperimentService experimentService;

  // TODO: add patch request: 'change status by sequence order'

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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable @Valid UUID id) {
    experimentService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/parameters")
  public ResponseEntity<List<UpdateExperimentParametersResponseDTO>> updateParameters(@PathVariable @Valid UUID id,
      @RequestBody @Valid List<UpdateExperimentParametersRequestDTO> dto) {
    return ResponseEntity.ok(experimentService.updateParameters(id, dto));
  }

  @PostMapping("/{id}/parameters")
  public ResponseEntity<List<CreateParameterResponseDTO>> addParameter(@PathVariable @Valid UUID id,
      @RequestBody @Valid CreateParameterRequestDTO dto) {
    return ResponseEntity.ok(experimentService.addParameter(id, dto));
  }

  @DeleteMapping("/{experimentId}/parameters/{parameterId}")
  public ResponseEntity<Void> removeParameter(@PathVariable @Valid UUID experimentId,
      @PathVariable @Valid UUID parameterId) {
    experimentService.removeParameter(experimentId, parameterId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/advance-status")
  public ResponseEntity<AdvanceExperimentStatusResponseDTO> advanceStatus(@PathVariable @Valid UUID id) {
    return ResponseEntity.ok(experimentService.advanceStatus(id));
  }
}
