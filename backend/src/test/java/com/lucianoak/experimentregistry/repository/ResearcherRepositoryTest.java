package com.lucianoak.experimentregistry.repository;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private List<Researcher> availableResearchers;

    @BeforeEach
    void setUp() {
      availableResearchers = List.of(
          Researcher.builder()
              .name("John Doe")
              .email("john.doe@example.com")
              .build(),
          Researcher.builder()
              .name("John Smith")
              .email("john.smith@example.com")
              .build(),
          Researcher.builder()
              .name("John Williams")
              .email("john.williams@example.com")
              .build(),
          Researcher.builder()
              .name("Mary Johnathan")
              .email("mary.johnathan@example.com")
              .build(),
          Researcher.builder()
              .name("William Johnson")
              .email("william.johnson@example.com")
              .build(),
          Researcher.builder()
              .name("Michael Doe")
              .email("michael.doe@example.com")
              .build(),
          Researcher.builder()
              .name("MICHAEL Smith")
              .email("michael.smith@example.com")
              .build(),
          Researcher.builder()
              .name("michael Williams")
              .email("michael.williams@example.com")
              .build(),
          Researcher.builder()
              .name("MiChAeL Johnson")
              .email("michael.johnson@example.com")
              .build(),
          Researcher.builder()
              .name("Alice Bennett")
              .email("alice.bennett@example.com")
              .build(),
          Researcher.builder()
              .name("Bob Bennett")
              .email("bob.bennett@example.com")
              .build(),
          Researcher.builder()
              .name("Charlie Bennett")
              .email("charlie.bennett@example.com")
              .build(),
          Researcher.builder()
              .name("David Bennett")
              .email("david.bennett@example.com")
              .build());
    }

    @AfterEach
    void tearDown() {
      researcherRepository.deleteAll();
      entityManager.clear();
    }

    private List<Researcher> getResearchersByNames(String... names) {
      return List.of(names).stream()
          .map(name -> availableResearchers.stream()
              .filter(r -> r.getName().equals(name))
              .findFirst()
              .orElseThrow())
          .toList();
    }

    @Test
    void givenResearchersWithMatchingNames_whenSearchingByName_thenReturnsResearchers() {
      List<Researcher> researchers = getResearchersByNames(
          "John Doe",
          "John Smith",
          "John Williams");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("John");

      Assertions.assertEquals(
          researchers.stream()
              .map(Researcher::getName)
              .toList(),
          result.stream()
              .map(Researcher::getName)
              .toList());
    }

    @Test
    void givenResearchersWithPartialMatches_whenSearchingByName_thenReturnsResearchers() {
      List<Researcher> researchers = getResearchersByNames(
          "Mary Johnathan",
          "William Johnson");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("ohn");

      Assertions.assertEquals(
          researchers.stream()
              .map(Researcher::getName)
              .toList(),
          result.stream()
              .map(Researcher::getName)
              .toList());

    }

    @Test
    void givenResearchersWithMixedCaseNames_whenSearchingByName_thenReturnsResearchers() {
      List<Researcher> researchers = getResearchersByNames(
          "Michael Doe",
          "MiChAeL Johnson",
          "MICHAEL Smith",
          "michael Williams");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("michael");

      Assertions.assertEquals(
          researchers.stream()
              .map(Researcher::getName)
              .toList(),
          result.stream()
              .map(Researcher::getName)
              .toList());

    }

    @Test
    void givenResearchersWithNoMatchingNames_whenSearchingByName_thenReturnsEmptyList() {
      List<Researcher> researchers = getResearchersByNames(
          "John Doe",
          "John Smith",
          "John Williams",
          "Mary Johnathan",
          "William Johnson");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("Michael");

      Assertions.assertEquals(
          List.of(),
          result.stream()
              .map(Researcher::getName)
              .toList());

    }

    @Test
    void givenResearchersWithPrefixAndSubstringMatches_whenSearchingByName_thenPrioritizesPrefixMatches() {
      List<Researcher> researchers = getResearchersByNames(
          "John Doe",
          "John Smith",
          "Mary Johnathan",
          "William Johnson");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("John");

      Assertions.assertEquals(
          researchers.stream()
              .map(Researcher::getName)
              .toList(),
          result.stream()
              .map(Researcher::getName)
              .toList());
    }

    @Test
    void givenMultipleMatchingResearchers_whenSearchingByName_thenReturnsResearchersAlphabetically() {
      List<Researcher> researchers = getResearchersByNames(
          "Alice Bennett",
          "Bob Bennett",
          "Charlie Bennett",
          "David Bennett");

      researchers.forEach(entityManager::persist);
      entityManager.flush();

      List<Researcher> result = researcherRepository.searchByName("Bennett");

      Assertions.assertEquals(
          researchers.stream()
              .map(Researcher::getName)
              .toList(),
          result.stream()
              .map(Researcher::getName)
              .toList());
    }

  }

  @Nested
  class ExistsByEmailTests {

    @AfterEach
    void tearDown() {
      researcherRepository.deleteAll();
      entityManager.clear();
    }

    @Test
    void givenExistingEmail_whenCheckingIfEmailExists_thenReturnsTrue() {
      entityManager.persistAndFlush(
          Researcher.builder()
              .name("John Doe")
              .email("john@example.com")
              .build());

      boolean result = researcherRepository.existsByEmail("john@example.com");

      Assertions.assertTrue(result);
    }

    @Test
    void givenNonExistingEmail_whenCheckingIfEmailExists_thenReturnsFalse() {
      Assertions.assertFalse(researcherRepository.existsByEmail("john@example.com"));
    }
  }
}
