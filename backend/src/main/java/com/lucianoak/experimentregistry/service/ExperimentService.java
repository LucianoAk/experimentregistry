package com.lucianoak.experimentregistry.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.request.UpdateExperimentParametersRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.request.UpdateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindAllExperimentsResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindExperimentByIdResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.UpdateExperimentParametersResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.UpdateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.FindParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.exception.DuplicateExperimentTitleException;
import com.lucianoak.experimentregistry.exception.ExperimentNotFoundException;
import com.lucianoak.experimentregistry.exception.NoStatusAssociatedWithWorkflowException;
import com.lucianoak.experimentregistry.exception.ParameterNotFoundException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.exception.WorkflowNotFountException;
import com.lucianoak.experimentregistry.model.Experiment;
import com.lucianoak.experimentregistry.model.ExperimentStatus;
import com.lucianoak.experimentregistry.model.Parameter;
import com.lucianoak.experimentregistry.model.Researcher;
import com.lucianoak.experimentregistry.model.Workflow;
import com.lucianoak.experimentregistry.repository.ExperimentRepository;
import com.lucianoak.experimentregistry.repository.ParameterRepository;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;
import com.lucianoak.experimentregistry.repository.WorkflowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentService {

  private final ExperimentRepository experimentRepository;
  private final ResearcherRepository researcherRepository;
  private final WorkflowRepository workflowRepository;
  private final ParameterRepository parameterRepository;

  @Transactional
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
        .startDate(dto.startDate())
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
        savedExperiment.getStartDate(),
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

  public List<FindAllExperimentsResponseDTO> findAll() {
    return experimentRepository.findAll().stream().map(p -> new FindAllExperimentsResponseDTO(
        p.getId(),
        p.getTitle(),
        p.getStartDate(),
        p.getFinishDate(),
        p.getStatus().getName(),
        p.getResearcher().getName())).toList();
  }

  public FindExperimentByIdResponseDTO findById(UUID id) {
    Experiment experiment = experimentRepository.findById(id).orElseThrow(() -> new ExperimentNotFoundException(id));
    return new FindExperimentByIdResponseDTO(
        experiment.getId(),
        experiment.getTitle(),
        experiment.getStartDate(),
        experiment.getFinishDate(),
        experiment.getResult(),
        experiment.getWorkflow().getId(),
        new FindExperimentStatusResponseDTO(
            experiment.getStatus().getId(),
            experiment.getStatus().getName(),
            experiment.getStatus().getSequenceOrder()),
        experiment.getResearcher().getId(),
        experiment.getParameters().stream()
            .map(p -> new FindParameterResponseDTO(
                p.getId(),
                p.getName(),
                p.getMeasurement(),
                p.getUnit(),
                p.getDescription()))
            .toList()

    );
  }

  @Transactional
  public UpdateExperimentResponseDTO update(UUID id, UpdateExperimentRequestDTO dto) {
    Experiment experiment = experimentRepository.findById(id).orElseThrow(() -> new ExperimentNotFoundException(id));

    if (dto.title() != null && !dto.title().isBlank()) {
      experiment.setTitle(dto.title());
    }

    if (dto.startDate() != null) {
      experiment.setStartDate(dto.startDate());
    }

    if (dto.finishDate() != null) {
      experiment.setFinishDate(dto.finishDate());
    }

    if (dto.result() != null && !dto.result().isBlank()) {
      experiment.setResult(dto.result());
    }

    if (dto.workflowId() != null) {
      Workflow workflow = workflowRepository.findById(dto.workflowId())
          .orElseThrow(() -> new WorkflowNotFountException(dto.workflowId()));

      List<ExperimentStatus> statuses = workflow.getStatuses();
      if (statuses.isEmpty()) {
        throw new NoStatusAssociatedWithWorkflowException(dto.workflowId());
      }

      ExperimentStatus status = statuses.stream()
          .min(Comparator.comparingInt(ExperimentStatus::getSequenceOrder))
          .orElseThrow();

      experiment.setWorkflow(workflow);
      experiment.setStatus(status);
    }

    if (dto.researcherId() != null) {
      Researcher researcher = researcherRepository.findById(dto.researcherId())
          .orElseThrow(() -> new ResearcherNotFoundException(dto.researcherId()));
      experiment.setResearcher(researcher);
    }

    Experiment savedExperiment = experimentRepository.save(experiment);

    return new UpdateExperimentResponseDTO(
        savedExperiment.getId(),
        savedExperiment.getTitle(),
        savedExperiment.getStartDate(),
        savedExperiment.getFinishDate(),
        savedExperiment.getResult(),
        new FindWorkflowResponseDTO(
            savedExperiment.getWorkflow().getId(),
            savedExperiment.getWorkflow().getVersion(),
            savedExperiment.getWorkflow().getStatuses().stream()
                .map(s -> new FindExperimentStatusResponseDTO(
                    s.getId(),
                    s.getName(),
                    s.getSequenceOrder()))
                .toList(),
            savedExperiment.getWorkflow().getCreatedAt()),
        new FindExperimentStatusResponseDTO(
            savedExperiment.getStatus().getId(), savedExperiment.getStatus().getName(),
            savedExperiment.getStatus().getSequenceOrder()),
        new FindResearcherResponseDTO(
            savedExperiment.getResearcher().getId(), savedExperiment.getResearcher().getName(),
            savedExperiment.getResearcher().getEmail()));
  }

  @Transactional
  public List<UpdateExperimentParametersResponseDTO> updateParameters(UUID id,
      List<UpdateExperimentParametersRequestDTO> dto) {
    Experiment experiment = experimentRepository.findById(id).orElseThrow(() -> new ExperimentNotFoundException(id));

    List<Parameter> parameters = experiment.getParameters();
    for (UpdateExperimentParametersRequestDTO parameterDTO : dto) {
      Parameter parameter = parameters.stream().filter(p -> p.getId().equals(parameterDTO.id())).findFirst()
          .orElseThrow(() -> new ParameterNotFoundException(parameterDTO.id()));

      if (parameterDTO.name() != null && !parameterDTO.name().isBlank()) {
        parameter.setName(parameterDTO.name());
      }

      if (parameterDTO.measurement() != null) {
        parameter.setMeasurement(parameterDTO.measurement());
      }

      if (parameterDTO.unit() != null && !parameterDTO.unit().isBlank()) {
        parameter.setUnit(parameterDTO.unit());
      }

      if (parameterDTO.description() != null &&
          !parameterDTO.description().isBlank()) {
        parameter.setDescription(parameterDTO.description());
      }
    }

    Experiment savedExperiment = experimentRepository.save(experiment);

    return savedExperiment.getParameters().stream().map(p -> new UpdateExperimentParametersResponseDTO(
        p.getId(),
        p.getName(),
        p.getMeasurement(),
        p.getUnit(),
        p.getDescription()))
        .toList();
  }

  @Transactional
  public void removeParameter(UUID experimentId, UUID parameterId) {
    Experiment experiment = experimentRepository.findById(experimentId)
        .orElseThrow(() -> new ExperimentNotFoundException(experimentId));

    Parameter parameter = parameterRepository.findById(parameterId).orElseThrow(
        () -> new ParameterNotFoundException(parameterId));

    experiment.removeParameter(parameter);
  }
}
