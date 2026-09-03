package com.lucianoak.experimentregistry.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.dto.researcher.request.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.repository.ResearcherRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ResearcherIntegrationTest {

  @Container
  @ServiceConnection
  private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ResearcherRepository researcherRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String BASE_URL = "/api/researchers";

  @Nested
  class CreateTests {

    @AfterEach
    void tearDown() {
      researcherRepository.deleteAll();
    }

    @Test
    void givenValidData_whenCreatingResearcher_thenReturnsCreatedResearcher() throws JacksonException, Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "john@example.com");

      MvcResult result = mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpectAll(
              MockMvcResultMatchers.status().isCreated(),
              MockMvcResultMatchers.jsonPath("$.id").exists(),
              MockMvcResultMatchers.jsonPath("$.name").value(dto.name()),
              MockMvcResultMatchers.jsonPath("$.email").value(dto.email()))
          .andReturn();

      CreateResearcherResponseDTO response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          CreateResearcherResponseDTO.class);

      Assertions.assertThat(researcherRepository.findById(response.id()))
          .isPresent()
          .get()
          .satisfies(researcher -> {
            Assertions.assertThat(researcher.getName()).isEqualTo(dto.name());
            Assertions.assertThat(researcher.getEmail()).isEqualTo(dto.email());
          });
    }

    // TODO:
    // givenDuplicateEmail_whenCreatingResearcher_thenReturnsConflict()
    // givenInvalidData_whenCreatingResearcher_thenReturnsBadRequest()
  }

  @Nested
  class FindByIdTests {
    // TODO:
    // givenExistingResearcher_whenFindingById_thenReturnsResearcher()
    // givenNonExistingId_whenFindingById_thenReturnsNotFound()
    // givenInvalidId_whenFindingById_thenReturnsBadRequest()
  }

  @Nested
  class SearchByNameTests {
    // TODO:
    // givenMatchingResearchers_whenSearchingByName_thenReturnsResearchers()
    // givenCaseInsensitiveName_whenSearchingByName_thenReturnsResearchers()
    // givenPartialName_whenSearchingByName_thenReturnsResearchers()
    // givenMatchingResearchers_whenSearchingByName_thenReturnsResearchersInExpectedOrder()
    // givenNonMatchingName_whenSearchingByName_thenReturnsEmptyList()
    // givenEmptyName_whenSearchingByName_thenReturnsBadRequest()
    // givenNameExceedingMaxLength_whenSearchingByName_thenReturnsBadRequest()
  }

  @Nested
  class CheckEmailAvailabilityTests {
    // TODO:
    // givenAvailableEmail_whenCheckingEmailAvailability_thenReturnsAvailable()
    // givenExistingEmail_whenCheckingEmailAvailability_thenReturnsUnavailable()
    // givenInvalidEmail_whenCheckingEmailAvailability_thenReturnsBadRequest()
    // givenBlankEmail_whenCheckingEmailAvailability_thenReturnsBadRequest()
  }

  @Nested
  class DeleteTests {
    // TODO:
    // givenExistingResearcher_whenDeletingResearcher_thenReturnsNoContent()
    // givenNonExistingId_whenDeletingResearcher_thenReturnsNotFound()
  }
}
