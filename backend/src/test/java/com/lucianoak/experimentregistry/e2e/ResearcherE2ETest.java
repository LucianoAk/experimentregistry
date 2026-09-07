package com.lucianoak.experimentregistry.e2e;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.dto.researcher.request.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.EmailAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.SearchResearcherResponseDTO;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class ResearcherE2ETest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  private final RestClient restClient;

  ResearcherE2ETest(RestClient.Builder restClientBuilder, @LocalServerPort int port) {
    this.restClient = RestClient.builder().baseUrl("http://localhost:" + port + "/api/researchers").build();
  }

  @Test
  void givenNewResearcherPayload_whenExecutingCompleteLifecycleJourney_thenStateTransitionsVerified() {
    CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
        "John Doe",
        "john@example.com");

    verifyEmailAvailable(dto);
    CreateResearcherResponseDTO created = createResearcher(dto);
    verifyResearcherFoundById(created);
    verifyResearcherFoundBySearch(created);
    deleteResearcher(created.id());
  }

  private void verifyEmailAvailable(CreateResearcherRequestDTO dto) {
    ResponseEntity<EmailAvailabilityResponseDTO> response = restClient.get()
        .uri(uriBuilder -> uriBuilder.path("/email-availability").queryParam("email", dto.email()).build())
        .retrieve().toEntity(EmailAvailabilityResponseDTO.class);

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      softly.assertThat(response.getBody()).isNotNull();
      softly.assertThat(response.getBody().available()).isTrue();
    });
  }

  private CreateResearcherResponseDTO createResearcher(CreateResearcherRequestDTO dto) {
    ResponseEntity<CreateResearcherResponseDTO> response = restClient.post()
        .accept(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .toEntity(CreateResearcherResponseDTO.class);

    CreateResearcherResponseDTO body = response.getBody();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      softly.assertThat(body).isNotNull();
      softly.assertThat(body.id()).isNotNull();
      softly.assertThat(body.name()).isEqualTo(dto.name());
      softly.assertThat(body.email()).isEqualTo(dto.email());
    });
    return body;
  }

  private void verifyResearcherFoundById(CreateResearcherResponseDTO dto) {
    ResponseEntity<FindResearcherResponseDTO> response = restClient.get()
        .uri(uriBuilder -> uriBuilder.path("/{id}").build(dto.id()))
        .retrieve()
        .toEntity(FindResearcherResponseDTO.class);

    FindResearcherResponseDTO body = response.getBody();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      softly.assertThat(body).isNotNull();
      softly.assertThat(body.id()).isEqualTo(dto.id());
      softly.assertThat(body.name()).isEqualTo(dto.name());
      softly.assertThat(body.email()).isEqualTo(dto.email());
    });
  }

  private void verifyResearcherFoundBySearch(CreateResearcherResponseDTO dto) {
    ResponseEntity<List<SearchResearcherResponseDTO>> response = restClient.get()
        .uri(uriBuilder -> uriBuilder.path("/search").queryParam("name", "John").build())
        .retrieve()
        .toEntity(new ParameterizedTypeReference<>() {
        });

    List<SearchResearcherResponseDTO> body = response.getBody();
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      softly.assertThat(body).isNotNull();
      softly.assertThat(body).extracting(SearchResearcherResponseDTO::name).contains(dto.name());
    });
  }

  private void deleteResearcher(UUID id) {
    ResponseEntity<Void> response = restClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/{id}").build(id))
        .retrieve()
        .toEntity(Void.class);

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      softly.assertThat(response.getBody()).isNull();
    });
  }
}
