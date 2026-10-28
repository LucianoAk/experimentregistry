package com.lucianoak.experimentregistry.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lucianoak.experimentregistry.repository.ExperimentRepository;

@ExtendWith(MockitoExtension.class)
public class ExperimentServiceTest {

  @Mock
  private ExperimentRepository experimentRepository;

  @InjectMocks
  private ExperimentService experimentService;

}
