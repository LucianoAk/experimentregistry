package com.lucianoak.experimentregistry.controller;

import com.lucianoak.experimentregistry.dto.experimentstatus.request.CreateExperimentStatusRequestDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.request.UpdateExperimentStatusesRequestDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.CreateExperimentStatuesResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.UpdateExperimentStatusesResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.request.CreateWorkflowRequestDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindAllWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.service.WorkflowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowService workflowService;

  @PostMapping
  public ResponseEntity<CreateWorkflowResponseDTO> create(
      @RequestBody @Valid CreateWorkflowRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.create(dto));
  }

  @GetMapping
  public ResponseEntity<List<FindAllWorkflowResponseDTO>> findAll() {
    return ResponseEntity.ok(workflowService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<FindWorkflowResponseDTO> findById(@PathVariable @Valid UUID id) {
    return ResponseEntity.ok(workflowService.findById(id));
  }

  // TODO: add verification to delete only when no experiments are associated with the workflow
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    workflowService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/statuses")
  public ResponseEntity<List<UpdateExperimentStatusesResponseDTO>> updateExperimentStatues(
      @PathVariable @Valid UUID id,
      @RequestBody List<@Valid UpdateExperimentStatusesRequestDTO> dtos) {
    return ResponseEntity.ok(workflowService.updateExperimentStatuses(id, dtos));
  }

  @PostMapping("/{id}/statuses/{position}")
  public ResponseEntity<List<CreateExperimentStatuesResponseDTO>> addExperimentStatus(
      @PathVariable @Valid UUID id,
      @PathVariable @Min(value = 0) Integer position,
      @RequestBody @Valid CreateExperimentStatusRequestDTO dto) {
    return ResponseEntity.ok(workflowService.addExperimentStatus(id, position, dto));
  }

  // TODO: add verification to delete only when no experiments are associated with the status
  @DeleteMapping("/{workflowId}/statuses/{statusId}")
  public ResponseEntity<List<FindExperimentStatusResponseDTO>> removeExperimentStatus(
      @PathVariable UUID workflowId, @PathVariable UUID statusId) {
    return ResponseEntity.ok(workflowService.removeExperimentStatus(workflowId, statusId));
  }
}
