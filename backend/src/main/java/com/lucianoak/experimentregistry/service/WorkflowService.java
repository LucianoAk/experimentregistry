package com.lucianoak.experimentregistry.service;

import com.lucianoak.experimentregistry.dto.experimentstatus.request.UpdateExperimentStatusesRequestDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.CreateExperimentStatuesResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.UpdateExperimentStatusesResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.request.CreateWorkflowRequestDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindAllWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.exception.StatusNotAssociatedWithWorkflowException;
import com.lucianoak.experimentregistry.exception.WorkflowNotFoundException;
import com.lucianoak.experimentregistry.model.ExperimentStatus;
import com.lucianoak.experimentregistry.model.Workflow;
import com.lucianoak.experimentregistry.repository.WorkflowRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowService {

  private final WorkflowRepository workflowRepository;

  @Transactional
  public CreateWorkflowResponseDTO create(CreateWorkflowRequestDTO dto) {
    Integer nextVersion =
        workflowRepository.findTopByOrderByVersionDesc().map(Workflow::getVersion).orElse(0) + 1;

    Workflow workflow = Workflow.builder().version(nextVersion).build();

    dto.statuses()
        .forEach(s -> workflow.addStatus(ExperimentStatus.builder().name(s.name()).build()));

    workflowRepository.save(workflow);

    return new CreateWorkflowResponseDTO(
        workflow.getId(),
        workflow.getVersion(),
        workflow.getStatuses().stream()
            .map(s ->
                new CreateExperimentStatuesResponseDTO(
                    s.getId(), s.getName(), s.getSequenceOrder()))
            .toList());
  }

  public List<FindAllWorkflowResponseDTO> findAll() {
    return workflowRepository.findAllByOrderByVersionDesc().stream()
        .map(w -> new FindAllWorkflowResponseDTO(w.getId(), w.getVersion(), w.getCreatedAt()))
        .toList();
  }

  public FindWorkflowResponseDTO findById(UUID id) {
    Workflow workflow =
        workflowRepository.findById(id).orElseThrow(() -> new WorkflowNotFoundException(id));

    return new FindWorkflowResponseDTO(
        workflow.getId(),
        workflow.getVersion(),
        workflow.getStatuses().stream()
            .map(s ->
                new FindExperimentStatusResponseDTO(s.getId(), s.getName(), s.getSequenceOrder()))
            .toList(),
        workflow.getCreatedAt());
  }

  @Transactional
  public List<UpdateExperimentStatusesResponseDTO> updateExperimentStatuses(
      UUID id, List<UpdateExperimentStatusesRequestDTO> dtos) {

    Set<Integer> seenOrders = new HashSet<>();
    for (UpdateExperimentStatusesRequestDTO dto : dtos) {
      if (dto.sequenceOrder() != null) {
        if (!seenOrders.add(dto.sequenceOrder())) {
          throw new InvalidEndpointRequestException(
              "Invalid experiment status request", "Duplicate sequence order found in request");
        }
      }
    }

    Workflow workflow =
        workflowRepository.findById(id).orElseThrow(() -> new WorkflowNotFoundException(id));

    List<ExperimentStatus> statuses = new ArrayList<>(workflow.getStatuses());
    statuses.sort(Comparator.comparing(ExperimentStatus::getSequenceOrder));

    for (UpdateExperimentStatusesRequestDTO statusDTO : dtos) {
      ExperimentStatus status =
          statuses.stream()
              .filter(s -> s.getId().equals(statusDTO.id()))
              .findFirst()
              .orElseThrow(() ->
                  new StatusNotAssociatedWithWorkflowException(statusDTO.id(), workflow.getId()));

      status.setName(statusDTO.name());

      if (statusDTO.sequenceOrder() != null
          && !statusDTO.sequenceOrder().equals(status.getSequenceOrder())) {
        int newOrder = statusDTO.sequenceOrder();

        if (newOrder < 0 || newOrder >= statuses.size()) {
          throw new InvalidEndpointRequestException(
              "Invalid experiment status request", "Sequence order out of bounds: " + newOrder);
        }

        int oldIndex = statuses.indexOf(status);

        if (oldIndex != newOrder) {
          statuses.remove(oldIndex);
          statuses.add(newOrder, status);
        }
      }
    }

    for (int i = 0; i < statuses.size(); i++) {
      statuses.get(i).setSequenceOrder(i);
    }

    workflowRepository.save(workflow);

    return workflow.getStatuses().stream()
        .map(s ->
            new UpdateExperimentStatusesResponseDTO(s.getId(), s.getName(), s.getSequenceOrder()))
        .toList();
  }
}
