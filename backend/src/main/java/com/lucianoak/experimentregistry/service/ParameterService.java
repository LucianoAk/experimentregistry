package com.lucianoak.experimentregistry.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.exception.ParameterNotFoundException;
import com.lucianoak.experimentregistry.model.Parameter;
import com.lucianoak.experimentregistry.repository.ParameterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParameterService {

  private final ParameterRepository parameterRepository;

  @Transactional
  public void delete(UUID id) {
    Parameter parameter = parameterRepository.findById(id).orElseThrow(
        () -> new ParameterNotFoundException(id));
    parameterRepository.delete(parameter);
  }
}
