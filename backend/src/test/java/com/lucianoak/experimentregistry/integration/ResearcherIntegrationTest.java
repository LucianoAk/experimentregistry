package com.lucianoak.experimentregistry.integration;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
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
import com.lucianoak.experimentregistry.model.Researcher;
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

  @AfterEach
  void tearDown() {
    researcherRepository.deleteAll();
  }

  @Nested
  class CreateTests {
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

    @Test
    void givenDuplicateEmail_whenCreatingResearcher_thenReturnsConflict() throws JacksonException, Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "john@example.com");

      researcherRepository.save(
          Researcher.builder()
              .name(dto.name())
              .email(dto.email())
              .build());

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void givenInvalidData_whenCreatingResearcher_thenReturnsBadRequest() throws JacksonException, Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "not-a-valid-email");

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
  }

  @Nested
  class FindByIdTests {
    @Test
    void givenExistingResearcher_whenFindingById_thenReturnsResearcher() throws Exception {
      Researcher researcher = researcherRepository.save(
          Researcher.builder()
              .name("John Doe")
              .email("john@example.com")
              .build());

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", researcher.getId()))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.id").value(researcher.getId().toString()),
              MockMvcResultMatchers.jsonPath("$.name").value(researcher.getName()),
              MockMvcResultMatchers.jsonPath("$.email").value(researcher.getEmail()));

    }

    @Test
    void givenNonExistingId_whenFindingById_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();
      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void givenInvalidId_whenFindingById_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-uuid";
      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
  }

  @Nested
  class SearchByNameTests {
    @Test
    void givenMatchingResearchers_whenSearchingByName_thenReturnsResearchers() throws Exception {
      researcherRepository.saveAll(
          List.of(
              Researcher.builder()
                  .name("John Doe")
                  .email("john@example.com")
                  .build(),
              Researcher.builder()
                  .name("Jane Doe")
                  .email("jane@example.com")
                  .build()));

      String name = "Doe";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(2),
              MockMvcResultMatchers.jsonPath("$[*].id").exists(),
              MockMvcResultMatchers.jsonPath("$[*].name",
                  Matchers.contains("Jane Doe", "John Doe")));
    }

    @Test
    void givenNonMatchingName_whenSearchingByName_thenReturnsEmptyList() throws Exception {
      String name = "Doe";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void givenEmptyName_whenSearchingByName_thenReturnsBadRequest() throws Exception {
      String name = "";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }

  @Nested
  class CheckEmailAvailabilityTests {
    @Test
    void givenAvailableEmail_whenCheckingEmailAvailability_thenReturnsAvailable() throws Exception {
      String email = "john@example.com";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", email))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    void givenUnavailableEmail_whenCheckingEmailAvailability_thenReturnsUnavailable() throws Exception {
      Researcher researcher = researcherRepository.save(
          Researcher.builder()
              .name("John Doe")
              .email("john@example.com")
              .build());

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", researcher.getEmail()))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(false));
    }

    // TODO:
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
