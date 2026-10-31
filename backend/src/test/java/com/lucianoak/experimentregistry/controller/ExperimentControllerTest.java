package com.lucianoak.experimentregistry.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ExperimentService experimentService;

  private static final String BASE_URL = "/api/experiments";

  @Nested
  class CreateTests {
    @Test
    void givenValidData_whenCreatingExperiment_thenReturnsCreatedExperiment() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Ambient temperature");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          "Test Experiment",
          LocalDate.now(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          List.of(parameterRequest));

      UUID experimentId = UUID.randomUUID();
      UUID workflowId = dto.workflowId();
      UUID researcherId = dto.researcherId();
      UUID parameterId = UUID.randomUUID();
      UUID statusId = UUID.randomUUID();

      CreateParameterResponseDTO parameterResponse = new CreateParameterResponseDTO(
          parameterId,
          parameterRequest.name(),
          parameterRequest.measurement(),
          parameterRequest.unit(),
          parameterRequest.description());

      FindWorkflowResponseDTO workflowResponse = new FindWorkflowResponseDTO(
          workflowId,
          1,
          List.of(),
          Instant.now());

      FindExperimentStatusResponseDTO statusResponse = new FindExperimentStatusResponseDTO(
          statusId,
          "PENDING",
          1);

      FindResearcherResponseDTO researcherResponse = new FindResearcherResponseDTO(
          researcherId,
          "John Doe",
          "john@example.com");

      CreateExperimentResponseDTO response = new CreateExperimentResponseDTO(
          experimentId,
          dto.title(),
          dto.startDate(),
          null,
          null,
          workflowResponse,
          statusResponse,
          researcherResponse,
          List.of(parameterResponse));

      Mockito.when(experimentService.create(dto)).thenReturn(response);

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpectAll(
              MockMvcResultMatchers.status().isCreated(),
              MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
              MockMvcResultMatchers.jsonPath("$.id").value(experimentId.toString()),
              MockMvcResultMatchers.jsonPath("$.title").value(dto.title()),
              MockMvcResultMatchers.jsonPath("$.startDate").value(dto.startDate().toString()),
              MockMvcResultMatchers.jsonPath("$.workflow.id").value(workflowId.toString()),
              MockMvcResultMatchers.jsonPath("$.researcher.id").value(researcherId.toString()),
              MockMvcResultMatchers.jsonPath("$.parameters.length()").value(1),
              MockMvcResultMatchers.jsonPath("$.parameters[0].id").value(parameterId.toString()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].name").value(parameterRequest.name()));

      Mockito.verify(experimentService).create(dto);
    }

    @Test
    void givenMalformedJson_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content("""
                  {
                  "title": "Test Experiment",
                  "startDate": "2026-06-01"
                  """)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingBodyContent_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingTitle_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Desc");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          null,
          LocalDate.now(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          List.of(parameterRequest));

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenTitleExceedingMaxLength_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Desc");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          "a".repeat(256),
          LocalDate.now(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          List.of(parameterRequest));

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingStartDate_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Desc");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          "Test Experiment",
          null,
          UUID.randomUUID(),
          UUID.randomUUID(),
          List.of(parameterRequest));

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingWorkflowId_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Desc");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          "Test Experiment",
          LocalDate.now(),
          null,
          UUID.randomUUID(),
          List.of(parameterRequest));

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingResearcherId_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest = new CreateParameterRequestDTO(
          "Temperature",
          BigDecimal.valueOf(25.5),
          "Celsius",
          "Desc");

      CreateExperimentRequestDTO dto = new CreateExperimentRequestDTO(
          "Test Experiment",
          LocalDate.now(),
          UUID.randomUUID(),
          null,
          List.of(parameterRequest));

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }
}
