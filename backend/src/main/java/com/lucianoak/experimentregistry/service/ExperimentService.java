package com.lucianoak.experimentregistry.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.exception.DuplicateExperimentTitleException;
import com.lucianoak.experimentregistry.exception.NoStatusAssociatedWithWorkflowException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.exception.WorkflowNotFountException;
import com.lucianoak.experimentregistry.model.Experiment;
import com.lucianoak.experimentregistry.model.ExperimentStatus;
import com.lucianoak.experimentregistry.model.Parameter;
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

    Experiment experiment = Experiment.builder()
        .title(dto.title())
        .workflow(workflow)
        .status(status)
        .researcher(researcher)
        .build();

    dto.parameters().forEach(p -> experiment.addParameter(
        Parameter.builder()
            .name(p.name())
            .measurement(p.measurament())
            .unit(p.unit())
            .description(p.description())
            .build()));

    Experiment savedExperiment = experimentRepository.save(experiment);

    return new CreateExperimentResponseDTO(
        savedExperiment.getId(),
        savedExperiment.getTitle(),
        "v" + savedExperiment.getWorkflow().getVersion(),
        savedExperiment.getStatus().getName(),
        savedExperiment.getResearcher().getName(),
        savedExperiment.getParameters().stream()
            .map(p -> new CreateParameterResponseDTO(
                p.getId(),
                p.getName(),
                p.getMeasurement(),
                p.getUnit(),
                p.getDescription()))
            .toList());
  }

  public TitleAvailabilityResponseDTO checkTitleAvailability(String title) {
    return new TitleAvailabilityResponseDTO(
        !experimentRepository.existsByTitle(title));
  }
}
