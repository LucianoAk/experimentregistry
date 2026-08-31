package com.lucianoak.experimentregistry.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.configuration.JpaConfig;
import com.lucianoak.experimentregistry.model.Researcher;

@DataJpaTest
@Testcontainers
@Import(JpaConfig.class)
public class ResearcherRepositoryTest {

  @Container
  @ServiceConnection
  private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

  @Autowired
  private ResearcherRepository researcherRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Nested
  class SearchByNameTests {

    @Test
    void givenResearchersWithMatchingNames_whenSearchingByName_thenReturnsResearchers() {
      Researcher researcher1 = Researcher.builder()
          .name("John Doe")
          .email("john@example.com")
          .build();

      Researcher researcher2 = Researcher.builder()
          .name("William Johnathan")
          .email("william@example.com")
          .build();

      entityManager.persist(researcher1);
      entityManager.persist(researcher2);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("John");

      Assertions.assertEquals(
          List.of(
              researcher1.getName(),
              researcher2.getName()),
          result.stream()
              .map(Researcher::getName)
              .toList());

    }

    // @Test
    void givenResearchersWithPartialMatches_whenSearchingByName_thenReturnsResearchers() {
    }

    // @Test
    void givenResearchersWithMixedCaseNames_whenSearchingByName_thenReturnsResearchers() {
    }

    // @Test
    void givenResearchersWithNoMatchingNames_whenSearchingByName_thenReturnsEmptyList() {
    }

    // @Test
    void givenResearchersWithPrefixAndSubstringMatches_whenSearchingByName_thenPrioritizesPrefixMatches() {
    }

    // @Test
    void givenMultipleMatchingResearchers_whenSearchingByName_thenReturnsResearchersAlphabetically() {
    }

    // @Test
    void givenExistingEmail_whenCheckingIfEmailExists_thenReturnsTrue() {
    }

    // @Test
    void givenNonExistingEmail_whenCheckingIfEmailExists_thenReturnsFalse() {
    }

  }
}
