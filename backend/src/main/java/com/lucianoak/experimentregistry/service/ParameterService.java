package com.lucianoak.experimentregistry.service;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.exception.ExperimentNotFoundException;
import com.lucianoak.experimentregistry.exception.ParameterAlreadyExistsInExperimentException;
import com.lucianoak.experimentregistry.model.Experiment;
import com.lucianoak.experimentregistry.model.Parameter;
import com.lucianoak.experimentregistry.repository.ExperimentRepository;
import com.lucianoak.experimentregistry.repository.ParameterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParameterService {

  private final ParameterRepository parameterRepository;
  private final ExperimentRepository experimentRepository;

  @Transactional
  public CreateParameterResponseDTO create(CreateParameterRequestDTO dto) {

    Experiment experiment = experimentRepository.findById(dto.experimentId()).orElseThrow(
        () -> new ExperimentNotFoundException(dto.experimentId()));

    if (parameterRepository.existsByExperimentAndName(experiment, dto.name())) {
      throw new ParameterAlreadyExistsInExperimentException(dto.name(), experiment.getId());
    }

    Parameter parameter = parameterRepository.save(Parameter.builder()
        .experiment(experiment)
        .name(dto.name())
        .measurement(dto.measurament())
        .unit(dto.unit())
        .description(dto.description())
        .build());

    return new CreateParameterResponseDTO(
        parameter.getId(),
        parameter.getName(),
        parameter.getMeasurement(),
        parameter.getUnit(),
        parameter.getDescription());
  }

}
