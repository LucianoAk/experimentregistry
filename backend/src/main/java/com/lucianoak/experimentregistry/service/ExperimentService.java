package com.lucianoak.experimentregistry.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.exception.DuplicateExperimentTitleException;
import com.lucianoak.experimentregistry.exception.NoStatusAssociatedWithWorkflowException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.exception.WorkflowNotFountException;
import com.lucianoak.experimentregistry.model.Experiment;
import com.lucianoak.experimentregistry.model.ExperimentStatus;
import com.lucianoak.experimentregistry.model.Researcher;
import com.lucianoak.experimentregistry.model.Workflow;
import com.lucianoak.experimentregistry.repository.ExperimentRepository;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;
import com.lucianoak.experimentregistry.repository.WorkflowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentService {

  private final ExperimentRepository experimentRepository;
  private final ResearcherRepository researcherRepository;
  private final WorkflowRepository workflowRepository;

  public CreateExperimentResponseDTO create(CreateExperimentRequestDTO dto) {
    if (experimentRepository.existsByTitle(dto.title())) {
      throw new DuplicateExperimentTitleException(dto.title());
    }
    Researcher researcher = researcherRepository.findById(dto.researcherId())
        .orElseThrow(() -> new ResearcherNotFoundException(dto.researcherId()));

    Workflow workflow = workflowRepository.findById(dto.workflowId())
        .orElseThrow(() -> new WorkflowNotFountException(dto.workflowId()));

    List<ExperimentStatus> statuses = workflow.getStatuses();
    if (statuses.isEmpty()) {
      throw new NoStatusAssociatedWithWorkflowException(dto.workflowId());
    }

    ExperimentStatus status = statuses.stream()
        .min(Comparator.comparingInt(ExperimentStatus::getSequenceOrder))
        .orElseThrow();

    Experiment experiment = experimentRepository.save(
        Experiment.builder()
            .title(dto.title())
            .workflow(workflow)
            .status(status)
            .researcher(researcher)
            .build());

    return new CreateExperimentResponseDTO(
        experiment.getId(),
        experiment.getTitle(),
        "v" + experiment.getWorkflow().getVersion(),
        experiment.getStatus().getName(),
        experiment.getResearcher().getName());
  }

  public TitleAvailabilityResponseDTO checkTitleAvailability(String tittle) {
    return new TitleAvailabilityResponseDTO(
        !experimentRepository.existsByTitle(tittle));
  }
}
