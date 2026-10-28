package com.lucianoak.experimentregistry.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.service.ExperimentService;

@WebMvcTest(ExperimentController.class)
public class ExperimentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ExperimentService experimentService;

  private static final String BASE_URL = "/api/experiments";

}
