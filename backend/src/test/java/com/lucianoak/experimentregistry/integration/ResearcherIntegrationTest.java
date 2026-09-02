package com.lucianoak.experimentregistry.integration;

import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.repository.ResearcherRepository;

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

  @Nested
  class CreateTests {
    // TODO:
    // givenValidData_whenCreatingResearcher_thenReturnsCreatedResearcher()
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
