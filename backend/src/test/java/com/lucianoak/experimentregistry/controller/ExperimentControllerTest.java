package com.lucianoak.experimentregistry.controller;

import com.lucianoak.experimentregistry.dto.experiment.request.CreateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.request.UpdateExperimentRequestDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.CreateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindAllExperimentsResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.FindExperimentByIdResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.TitleAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.experiment.response.UpdateExperimentResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.request.UpdateExperimentParametersRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.FindParameterResponseDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.UpdateExperimentParametersResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.exception.ExperimentNotFoundException;
import com.lucianoak.experimentregistry.exception.ParameterNotFoundException;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.exception.WorkflowNotFoundException;
import com.lucianoak.experimentregistry.service.ExperimentService;
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
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ExperimentService experimentService;

  private static final String BASE_URL = "/api/experiments";

  @Nested
  class CreateTests {
    @Test
    void givenValidData_whenCreatingExperiment_thenReturnsCreatedExperiment() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "Temperature", BigDecimal.valueOf(25.5), "Celsius", "Ambient temperature");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
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

      CreateParameterResponseDTO parameterResponse =
          new CreateParameterResponseDTO(
              parameterId,
              parameterRequest.name(),
              parameterRequest.measurement(),
              parameterRequest.unit(),
              parameterRequest.description());

      FindWorkflowResponseDTO workflowResponse =
          new FindWorkflowResponseDTO(workflowId, 1, List.of(), Instant.now());

      FindExperimentStatusResponseDTO statusResponse =
          new FindExperimentStatusResponseDTO(statusId, "PENDING", 1);

      FindResearcherResponseDTO researcherResponse =
          new FindResearcherResponseDTO(researcherId, "John Doe", "john@example.com");

      CreateExperimentResponseDTO response =
          new CreateExperimentResponseDTO(
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

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
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
              MockMvcResultMatchers.jsonPath("$.parameters[0].name")
                  .value(parameterRequest.name()));

      Mockito.verify(experimentService).create(dto);
    }

    @Test
    void givenMalformedJson_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(
                      """
                      {
                      "title": "Test Experiment",
                      "startDate": "2026-06-01"
                      """)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingBodyContent_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      mockMvc
          .perform(MockMvcRequestBuilders.post(BASE_URL).contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingTitle_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              null,
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenBlankTitle_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "", LocalDate.now(), UUID.randomUUID(), UUID.randomUUID(), List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenTitleExceedingMaxLength_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "a".repeat(256),
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingStartDate_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              null,
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingWorkflowId_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              null,
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingResearcherId_whenCreatingExperiment_thenReturnsBadRequest() throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "Celsius", "Desc");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              null,
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithNullName_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(null, BigDecimal.valueOf(25.5), "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithBlankName_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("", BigDecimal.valueOf(25.5), "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithNameExceedingMaxLength_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "a".repeat(256), BigDecimal.valueOf(25.5), "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithNullMeasurement_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", null, "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void
        givenParameterWithExceedingIntegerDigitsMeasurement_whenCreatingExperiment_thenReturnsBadRequest()
            throws Exception {
      BigDecimal invalidMeasurement = new BigDecimal("12345678901234.123");

      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "Temperature", invalidMeasurement, "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void
        givenParameterWithExceedingFractionDigitsMeasurement_whenCreatingExperiment_thenReturnsBadRequest()
            throws Exception {
      BigDecimal invalidMeasurement = new BigDecimal("123.1234567");

      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "Temperature", invalidMeasurement, "Celsius", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithNullUnit_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "Temperature", BigDecimal.valueOf(25.5), null, "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithBlankUnit_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO("Temperature", BigDecimal.valueOf(25.5), "", "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenParameterWithUnitExceedingMaxLength_whenCreatingExperiment_thenReturnsBadRequest()
        throws Exception {
      CreateParameterRequestDTO parameterRequest =
          new CreateParameterRequestDTO(
              "Temperature", BigDecimal.valueOf(25.5), "a".repeat(51), "Description");

      CreateExperimentRequestDTO dto =
          new CreateExperimentRequestDTO(
              "Test Experiment",
              LocalDate.now(),
              UUID.randomUUID(),
              UUID.randomUUID(),
              List.of(parameterRequest));

      mockMvc
          .perform(
              MockMvcRequestBuilders.post(BASE_URL)
                  .content(objectMapper.writeValueAsString(dto))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }

  @Nested
  class CheckTitleAvailabilityTests {
    @Test
    void givenAvailableTitle_whenCheckingTitleAvailability_thenReturnsAvailable() throws Exception {
      String title = "Quantum Physics";

      Mockito.when(experimentService.checkTitleAvailability(title))
          .thenReturn(new TitleAvailabilityResponseDTO(true));

      mockMvc
          .perform(
              MockMvcRequestBuilders.get(BASE_URL + "/title-availability").param("title", title))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(true));

      Mockito.verify(experimentService).checkTitleAvailability(title);
    }

    @Test
    void givenUnavailableTitle_whenCheckingTitleAvailability_thenReturnsUnavailable()
        throws Exception {
      String title = "Quantum Physics";

      Mockito.when(experimentService.checkTitleAvailability(title))
          .thenReturn(new TitleAvailabilityResponseDTO(false));

      mockMvc
          .perform(
              MockMvcRequestBuilders.get(BASE_URL + "/title-availability").param("title", title))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(false));

      Mockito.verify(experimentService).checkTitleAvailability(title);
    }

    @Test
    void givenEmptyTitle_whenCheckingTitleAvailability_thenReturnsBadRequest() throws Exception {
      String title = "";

      mockMvc
          .perform(
              MockMvcRequestBuilders.get(BASE_URL + "/title-availability").param("title", title))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingTitle_whenCheckingTitleAvailability_thenReturnsBadRequest() throws Exception {
      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL + "/title-availability"))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }

  @Nested
  class FindAllTests {
    @Test
    void givenExperimentsExist_whenFindingAll_thenReturnsExperiments() throws Exception {
      List<FindAllExperimentsResponseDTO> experiments =
          List.of(
              new FindAllExperimentsResponseDTO(
                  UUID.randomUUID(),
                  "Quantum Physics Experiment",
                  LocalDate.of(2026, 1, 1),
                  LocalDate.of(2026, 1, 10),
                  new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Active", 1),
                  new FindResearcherResponseDTO(UUID.randomUUID(), "John Doe", "john@example.com")),
              new FindAllExperimentsResponseDTO(
                  UUID.randomUUID(),
                  "Chemistry Experiment",
                  LocalDate.of(2026, 2, 1),
                  LocalDate.of(2026, 2, 15),
                  new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Pending", 2),
                  new FindResearcherResponseDTO(
                      UUID.randomUUID(), "Jane Doe", "jane@example.com")));

      Mockito.when(experimentService.findAll()).thenReturn(experiments);

      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(2),
              MockMvcResultMatchers.jsonPath("$[0].id").value(experiments.get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].title").value(experiments.get(0).title()),
              MockMvcResultMatchers.jsonPath("$[0].startDate")
                  .value(experiments.get(0).startDate().toString()),
              MockMvcResultMatchers.jsonPath("$[0].finishDate")
                  .value(experiments.get(0).finishDate().toString()),
              MockMvcResultMatchers.jsonPath("$[0].status.id")
                  .value(experiments.get(0).status().id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].status.name")
                  .value(experiments.get(0).status().name()),
              MockMvcResultMatchers.jsonPath("$[0].status.sequenceOrder")
                  .value(experiments.get(0).status().sequenceOrder()),
              MockMvcResultMatchers.jsonPath("$[0].researcher.id")
                  .value(experiments.get(0).researcher().id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].researcher.name")
                  .value(experiments.get(0).researcher().name()),
              MockMvcResultMatchers.jsonPath("$[0].researcher.email")
                  .value(experiments.get(0).researcher().email()),
              MockMvcResultMatchers.jsonPath("$[1].id").value(experiments.get(1).id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].title").value(experiments.get(1).title()),
              MockMvcResultMatchers.jsonPath("$[1].startDate")
                  .value(experiments.get(1).startDate().toString()),
              MockMvcResultMatchers.jsonPath("$[1].finishDate")
                  .value(experiments.get(1).finishDate().toString()),
              MockMvcResultMatchers.jsonPath("$[1].status.id")
                  .value(experiments.get(1).status().id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].status.name")
                  .value(experiments.get(1).status().name()),
              MockMvcResultMatchers.jsonPath("$[1].status.sequenceOrder")
                  .value(experiments.get(1).status().sequenceOrder()),
              MockMvcResultMatchers.jsonPath("$[1].researcher.id")
                  .value(experiments.get(1).researcher().id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].researcher.name")
                  .value(experiments.get(1).researcher().name()),
              MockMvcResultMatchers.jsonPath("$[1].researcher.email")
                  .value(experiments.get(1).researcher().email()));

      Mockito.verify(experimentService).findAll();
    }

    @Test
    void givenNoExperimentsExist_whenFindingAll_thenReturnsEmptyList() throws Exception {
      Mockito.when(experimentService.findAll()).thenReturn(List.of());

      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(0));

      Mockito.verify(experimentService).findAll();
    }
  }

  @Nested
  class FindByIdTests {
    @Test
    void givenExistingId_whenFindingById_thenReturnsExperiment() throws Exception {
      UUID id = UUID.randomUUID();

      FindExperimentByIdResponseDTO experiment =
          new FindExperimentByIdResponseDTO(
              id,
              "Quantum Physics Experiment",
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              new FindWorkflowResponseDTO(
                  UUID.randomUUID(),
                  1,
                  List.of(new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Active", 1)),
                  Instant.parse("2026-01-01T00:00:00Z")),
              new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Active", 1),
              new FindResearcherResponseDTO(UUID.randomUUID(), "John Doe", "john@example.com"),
              List.of(
                  new FindParameterResponseDTO(
                      UUID.randomUUID(),
                      "Temperature",
                      BigDecimal.valueOf(25.5),
                      "Celsius",
                      "Room temperature")));

      Mockito.when(experimentService.findById(id)).thenReturn(experiment);

      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", id))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.id").value(id.toString()),
              MockMvcResultMatchers.jsonPath("$.title").value(experiment.title()),
              MockMvcResultMatchers.jsonPath("$.startDate")
                  .value(experiment.startDate().toString()),
              MockMvcResultMatchers.jsonPath("$.finishDate")
                  .value(experiment.finishDate().toString()),
              MockMvcResultMatchers.jsonPath("$.result").value(experiment.result()),
              MockMvcResultMatchers.jsonPath("$.workflow.id")
                  .value(experiment.workflow().id().toString()),
              MockMvcResultMatchers.jsonPath("$.workflow.version")
                  .value(experiment.workflow().version()),
              MockMvcResultMatchers.jsonPath("$.workflow.statues[0].id")
                  .value(experiment.workflow().statues().get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$.workflow.statues[0].name")
                  .value(experiment.workflow().statues().get(0).name()),
              MockMvcResultMatchers.jsonPath("$.workflow.statues[0].sequenceOrder")
                  .value(experiment.workflow().statues().get(0).sequenceOrder()),
              MockMvcResultMatchers.jsonPath("$.workflow.createdAt")
                  .value(experiment.workflow().createdAt().toString()),
              MockMvcResultMatchers.jsonPath("$.status.id")
                  .value(experiment.status().id().toString()),
              MockMvcResultMatchers.jsonPath("$.status.name").value(experiment.status().name()),
              MockMvcResultMatchers.jsonPath("$.status.sequenceOrder")
                  .value(experiment.status().sequenceOrder()),
              MockMvcResultMatchers.jsonPath("$.researcher.id")
                  .value(experiment.researcher().id().toString()),
              MockMvcResultMatchers.jsonPath("$.researcher.name")
                  .value(experiment.researcher().name()),
              MockMvcResultMatchers.jsonPath("$.researcher.email")
                  .value(experiment.researcher().email()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].id")
                  .value(experiment.parameters().get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].name")
                  .value(experiment.parameters().get(0).name()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].measurement")
                  .value(experiment.parameters().get(0).measurement().doubleValue()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].unit")
                  .value(experiment.parameters().get(0).unit()),
              MockMvcResultMatchers.jsonPath("$.parameters[0].description")
                  .value(experiment.parameters().get(0).description()));

      Mockito.verify(experimentService).findById(id);
    }

    @Test
    void givenNonExistingId_whenFindingById_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();

      Mockito.when(experimentService.findById(id)).thenThrow(new ExperimentNotFoundException(id));

      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService).findById(id);
    }

    @Test
    void givenInvalidId_whenFindingById_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";

      mockMvc
          .perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }

  @Nested
  class UpdateTests {
    @Test
    void givenValidData_whenUpdatingExperiment_thenReturnsOk() throws Exception {
      UUID id = UUID.randomUUID();
      UpdateExperimentRequestDTO dto =
          new UpdateExperimentRequestDTO(
              "Quantum Physics Experiment",
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              UUID.randomUUID(),
              UUID.randomUUID());

      UpdateExperimentResponseDTO response =
          new UpdateExperimentResponseDTO(
              id,
              dto.title(),
              dto.startDate(),
              dto.finishDate(),
              dto.result(),
              new FindWorkflowResponseDTO(
                  UUID.randomUUID(),
                  1,
                  List.of(new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Active", 1)),
                  Instant.parse("2026-01-01T00:00:00Z")),
              new FindExperimentStatusResponseDTO(UUID.randomUUID(), "Active", 1),
              new FindResearcherResponseDTO(UUID.randomUUID(), "John Doe", "john@example.com"));

      Mockito.when(experimentService.update(id, dto)).thenReturn(response);

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
              MockMvcResultMatchers.jsonPath("$.id").value(id.toString()),
              MockMvcResultMatchers.jsonPath("$.title").value(dto.title()),
              MockMvcResultMatchers.jsonPath("$.startDate").value(dto.startDate().toString()),
              MockMvcResultMatchers.jsonPath("$.finishDate").value(dto.finishDate().toString()),
              MockMvcResultMatchers.jsonPath("$.result").value(dto.result()),
              MockMvcResultMatchers.jsonPath("$.workflow.id")
                  .value(response.workflow().id().toString()),
              MockMvcResultMatchers.jsonPath("$.workflow.version")
                  .value(response.workflow().version()),
              MockMvcResultMatchers.jsonPath("$.status.id")
                  .value(response.status().id().toString()),
              MockMvcResultMatchers.jsonPath("$.status.name").value(response.status().name()),
              MockMvcResultMatchers.jsonPath("$.researcher.id")
                  .value(response.researcher().id().toString()),
              MockMvcResultMatchers.jsonPath("$.researcher.name")
                  .value(response.researcher().name()));

      Mockito.verify(experimentService).update(id, dto);
    }

    @Test
    void givenNonExistingExperiment_whenUpdatingExperiment_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();

      UpdateExperimentRequestDTO dto =
          new UpdateExperimentRequestDTO(
              "Quantum Physics Experiment",
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              UUID.randomUUID(),
              UUID.randomUUID());

      Mockito.when(
              experimentService.update(
                  Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class)))
          .thenThrow(new ExperimentNotFoundException(id));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService)
          .update(Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class));
    }

    @Test
    void givenInvalidId_whenUpdatingExperiment_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      objectMapper.writeValueAsString(
                          new UpdateExperimentRequestDTO(
                              "Quantum Physics Experiment",
                              LocalDate.of(2026, 1, 1),
                              LocalDate.of(2026, 1, 10),
                              "Successful results",
                              UUID.randomUUID(),
                              UUID.randomUUID()))))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingBodyContent_whenUpdatingExperiment_thenReturnsBadRequest() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void given256CharacterTitle_whenUpdatingExperiment_thenReturnsBadRequest() throws Exception {
      UUID id = UUID.randomUUID();

      UpdateExperimentRequestDTO dto =
          new UpdateExperimentRequestDTO(
              "a".repeat(256),
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              UUID.randomUUID(),
              UUID.randomUUID());

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenNonExistingWorkflow_whenUpdatingExperiment_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();
      UUID workflowId = UUID.randomUUID();

      UpdateExperimentRequestDTO dto =
          new UpdateExperimentRequestDTO(
              "Quantum Physics Experiment",
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              workflowId,
              UUID.randomUUID());

      Mockito.when(
              experimentService.update(
                  Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class)))
          .thenThrow(new WorkflowNotFoundException(workflowId));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService)
          .update(Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class));
    }

    @Test
    void givenNonExistingResearhcer_whenUpdatingExperiment_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();
      UUID researcherId = UUID.randomUUID();

      UpdateExperimentRequestDTO dto =
          new UpdateExperimentRequestDTO(
              "Quantum Physics Experiment",
              LocalDate.of(2026, 1, 1),
              LocalDate.of(2026, 1, 10),
              "Successful results",
              UUID.randomUUID(),
              researcherId);

      Mockito.when(
              experimentService.update(
                  Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class)))
          .thenThrow(new ResearcherNotFoundException(researcherId));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService)
          .update(Mockito.eq(id), Mockito.any(UpdateExperimentRequestDTO.class));
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void givenExistingId_whenDeletingExperiment_thenReturnsNoContent() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc
          .perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNoContent());

      Mockito.verify(experimentService).delete(id);
    }

    @Test
    void givenNonExistingId_whenDeletingExperiment_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();

      Mockito.doThrow(new ExperimentNotFoundException(id)).when(experimentService).delete(id);

      mockMvc
          .perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService).delete(id);
    }

    @Test
    void givenInvalidId_whenDeletingExperiment_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";

      mockMvc
          .perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }

  @Nested
  class UpdateParametersTests {
    @Test
    void givenValidData_whenUpdatingParameters_thenReturnsOk() throws Exception {
      UUID id = UUID.randomUUID();
      List<UpdateExperimentParametersRequestDTO> requestDtos =
          List.of(
              new UpdateExperimentParametersRequestDTO(
                  UUID.randomUUID(),
                  "Temperature",
                  BigDecimal.valueOf(25.5),
                  "Celsius",
                  "Room temperature"));

      List<UpdateExperimentParametersResponseDTO> responseDtos =
          List.of(
              new UpdateExperimentParametersResponseDTO(
                  requestDtos.get(0).id(),
                  requestDtos.get(0).name(),
                  requestDtos.get(0).measurement(),
                  requestDtos.get(0).unit(),
                  requestDtos.get(0).description()));

      Mockito.when(experimentService.updateParameters(id, requestDtos)).thenReturn(responseDtos);

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestDtos)))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
              MockMvcResultMatchers.jsonPath("$.length()").value(1),
              MockMvcResultMatchers.jsonPath("$[0].id").value(responseDtos.get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].name").value(responseDtos.get(0).name()),
              MockMvcResultMatchers.jsonPath("$[0].measurement")
                  .value(responseDtos.get(0).measurement().doubleValue()),
              MockMvcResultMatchers.jsonPath("$[0].unit").value(responseDtos.get(0).unit()),
              MockMvcResultMatchers.jsonPath("$[0].description")
                  .value(responseDtos.get(0).description()));

      Mockito.verify(experimentService).updateParameters(Mockito.eq(id), Mockito.anyList());
    }

    @Test
    void givenNonExistingExperiment_whenUpdatingParameters_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();
      List<UpdateExperimentParametersRequestDTO> requestDtos =
          List.of(
              new UpdateExperimentParametersRequestDTO(
                  UUID.randomUUID(),
                  "Temperature",
                  BigDecimal.valueOf(25.5),
                  "Celsius",
                  "Room temperature"));

      Mockito.when(experimentService.updateParameters(Mockito.eq(id), Mockito.anyList()))
          .thenThrow(new ExperimentNotFoundException(id));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestDtos)))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService).updateParameters(Mockito.eq(id), Mockito.anyList());
    }

    @Test
    void givenInvalidId_whenUpdatingParameters_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";
      List<UpdateExperimentParametersRequestDTO> requestDtos =
          List.of(
              new UpdateExperimentParametersRequestDTO(
                  UUID.randomUUID(),
                  "Temperature",
                  BigDecimal.valueOf(25.5),
                  "Celsius",
                  "Room temperature"));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestDtos)))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenMissingBodyContent_whenUpdatingParameters_thenReturnsBadRequest() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenNameExceedingMaxLength_whenUpdatingParameters_thenReturnsBadRequest()
        throws Exception {
      UUID id = UUID.randomUUID();
      List<UpdateExperimentParametersRequestDTO> requestDtos =
          List.of(
              new UpdateExperimentParametersRequestDTO(
                  UUID.randomUUID(),
                  "a".repeat(256),
                  BigDecimal.valueOf(25.5),
                  "Celsius",
                  "Room temperature"));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestDtos)))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenUnitExceedingMaxLength_whenUpdatingParameters_thenReturnsBadRequest()
        throws Exception {
      UUID id = UUID.randomUUID();
      List<UpdateExperimentParametersRequestDTO> requestDtos =
          List.of(
              new UpdateExperimentParametersRequestDTO(
                  UUID.randomUUID(),
                  "Temperature",
                  BigDecimal.valueOf(25.5),
                  "a".repeat(51),
                  "Room temperature"));

      mockMvc
          .perform(
              MockMvcRequestBuilders.put(BASE_URL + "/{id}/parameters", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestDtos)))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }

  @Nested
  class RemoveParameterTests {
    @Test
    void givenValidIds_whenRemovingParameter_thenReturnsNoContent() throws Exception {
      UUID experimentId = UUID.randomUUID();
      UUID parameterId = UUID.randomUUID();

      Mockito.doNothing().when(experimentService).removeParameter(experimentId, parameterId);

      mockMvc
          .perform(
              MockMvcRequestBuilders.delete(
                  BASE_URL + "/{experimentId}/parameters/{parameterId}", experimentId, parameterId))
          .andExpect(MockMvcResultMatchers.status().isNoContent());

      Mockito.verify(experimentService).removeParameter(experimentId, parameterId);
    }

    @Test
    void givenNonExistingExperimentId_whenRemovingParameter_thenReturnsNotFound() throws Exception {
      UUID experimentId = UUID.randomUUID();
      UUID parameterId = UUID.randomUUID();

      Mockito.doThrow(new ExperimentNotFoundException(experimentId))
          .when(experimentService)
          .removeParameter(experimentId, parameterId);

      mockMvc
          .perform(
              MockMvcRequestBuilders.delete(
                  BASE_URL + "/{experimentId}/parameters/{parameterId}", experimentId, parameterId))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService).removeParameter(experimentId, parameterId);
    }

    @Test
    void givenInvalidExperimentId_whenRemovingParameter_thenReturnsBadRequest() throws Exception {
      String experimentId = "not-a-valid-uuid";
      UUID parameterId = UUID.randomUUID();

      mockMvc
          .perform(
              MockMvcRequestBuilders.delete(
                  BASE_URL + "/{experimentId}/parameters/{parameterId}", experimentId, parameterId))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }

    @Test
    void givenNonExistingParameterId_whenRemovingParameter_thenReturnsNotFound() throws Exception {
      UUID experimentId = UUID.randomUUID();
      UUID parameterId = UUID.randomUUID();

      Mockito.doThrow(new ParameterNotFoundException(parameterId))
          .when(experimentService)
          .removeParameter(experimentId, parameterId);

      mockMvc
          .perform(
              MockMvcRequestBuilders.delete(
                  BASE_URL + "/{experimentId}/parameters/{parameterId}", experimentId, parameterId))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(experimentService).removeParameter(experimentId, parameterId);
    }

    @Test
    void givenInvalidParameterId_whenRemovingParameter_thenReturnsBadRequest() throws Exception {
      UUID experimentId = UUID.randomUUID();
      String parameterId = "not-a-valid-uuid";

      mockMvc
          .perform(
              MockMvcRequestBuilders.delete(
                  BASE_URL + "/{experimentId}/parameters/{parameterId}", experimentId, parameterId))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(experimentService);
    }
  }
}
