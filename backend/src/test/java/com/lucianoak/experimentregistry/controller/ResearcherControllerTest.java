package com.lucianoak.experimentregistry.controller;

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

import com.lucianoak.experimentregistry.dto.researcher.request.CreateResearcherRequestDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.CreateResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.EmailAvailabilityResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.FindResearcherResponseDTO;
import com.lucianoak.experimentregistry.dto.researcher.response.SearchResearcherResponseDTO;
import com.lucianoak.experimentregistry.exception.ResearcherNotFoundException;
import com.lucianoak.experimentregistry.service.ResearcherService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ResearcherController.class)
class ResearcherControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ResearcherService researcherService;

  private static final String BASE_URL = "/api/researchers";

  @Nested
  class SearchByNameTests {
    @Test
    void givenValidName_whenSearchingByName_thenReturnsResearchers() throws Exception {
      List<SearchResearcherResponseDTO> researchers = List.of(
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "John Doe",
              "john@example.com"),
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "Jane Doe",
              "jane@example.com"));

      String name = "Doe";

      Mockito.when(researcherService.searchByName(name)).thenReturn(researchers);

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(2),
              MockMvcResultMatchers.jsonPath("$[0].id").value(researchers.get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].name").value(researchers.get(0).name()),
              MockMvcResultMatchers.jsonPath("$[1].id").value(researchers.get(1).id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].name").value(researchers.get(1).name()));

      Mockito.verify(researcherService).searchByName(name);
    }

    @Test
    void givenEmptyName_whenSearchingByName_thenReturnsBadRequest() throws Exception {
      String name = "";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void given1CharacterName_whenSearchingByName_thenReturnsResearchers() throws Exception {
      List<SearchResearcherResponseDTO> researchers = List.of(
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "John Doe",
              "john@example.com"),
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "Jane Doe",
              "jane@example.com"));

      String name = "D";

      Mockito.when(researcherService.searchByName(name)).thenReturn(researchers);

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(2),
              MockMvcResultMatchers.jsonPath("$[0].id").value(researchers.get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].name").value(researchers.get(0).name()),
              MockMvcResultMatchers.jsonPath("$[0].email").value(researchers.get(0).email()),
              MockMvcResultMatchers.jsonPath("$[1].id").value(researchers.get(1).id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].name").value(researchers.get(1).name()),
              MockMvcResultMatchers.jsonPath("$[1].email").value(researchers.get(1).email()));

      Mockito.verify(researcherService).searchByName(name);
    }

    @Test
    void given255CharacterName_whenSearchingByName_thenReturnsResearchers() throws Exception {
      List<SearchResearcherResponseDTO> researchers = List.of(
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "John Doe",
              "john@example.com"),
          new SearchResearcherResponseDTO(
              UUID.randomUUID(),
              "Jane Doe",
              "jane@example.com"));

      String name = "a".repeat(255);

      Mockito.when(researcherService.searchByName(name)).thenReturn(researchers);

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.length()").value(2),
              MockMvcResultMatchers.jsonPath("$[0].id").value(researchers.get(0).id().toString()),
              MockMvcResultMatchers.jsonPath("$[0].name").value(researchers.get(0).name()),
              MockMvcResultMatchers.jsonPath("$[1].id").value(researchers.get(1).id().toString()),
              MockMvcResultMatchers.jsonPath("$[1].name").value(researchers.get(1).name()),
              MockMvcResultMatchers.jsonPath("$[0].email").value(researchers.get(0).email()),
              MockMvcResultMatchers.jsonPath("$[1].email").value(researchers.get(1).email()));

      Mockito.verify(researcherService).searchByName(name);
    }

    @Test
    void givenNameExceedingMaxLength_whenSearchingByName_thenReturnsBadRequest() throws Exception {
      String name = "a".repeat(256);

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search")
              .param("name", name))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingName_whenSearchingByName_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/search"))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);

    }
  }

  @Nested
  class FindByIdTests {
    @Test
    void givenExistingId_whenFindingById_thenReturnsResearcher() throws Exception {
      UUID id = UUID.randomUUID();

      FindResearcherResponseDTO researcher = new FindResearcherResponseDTO(
          id,
          "John Doe",
          "john@example.com");

      Mockito.when(researcherService.findById(id)).thenReturn(researcher);

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", id))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.id").value(id.toString()),
              MockMvcResultMatchers.jsonPath("$.name").value(researcher.name()),
              MockMvcResultMatchers.jsonPath("$.email").value(researcher.email()));

      Mockito.verify(researcherService).findById(id);
    }

    @Test
    void givenNonExistingId_whenFindingById_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();

      Mockito.when(researcherService.findById(id)).thenThrow(new ResearcherNotFoundException(id));

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(researcherService).findById(id);
    }

    @Test
    void givenInvalidId_whenFindingById_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingId_whenFindingById_thenReturnsBadRequest() throws Exception {

    }
  }

  @Nested
  class CheckEmailAvailabilityTests {
    @Test
    void givenAvailableEmail_whenCheckingEmailAvailability_thenReturnsAvailable() throws Exception {
      String email = "john@example.com";

      Mockito.when(researcherService.checkEmailAvailability(email)).thenReturn(
          new EmailAvailabilityResponseDTO(true));

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", email))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(true));

      Mockito.verify(researcherService).checkEmailAvailability(email);
    }

    @Test
    void givenUnavailableEmail_whenCheckingEmailAvailability_thenReturnsUnavailable() throws Exception {
      String email = "john@example.com";

      Mockito.when(researcherService.checkEmailAvailability(email)).thenReturn(
          new EmailAvailabilityResponseDTO(false));

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", email))
          .andExpectAll(
              MockMvcResultMatchers.status().isOk(),
              MockMvcResultMatchers.jsonPath("$.available").value(false));

      Mockito.verify(researcherService).checkEmailAvailability(email);
    }

    @Test
    void givenInvalidEmail_whenCheckingEmailAvailability_thenReturnsBadRequest() throws Exception {
      String email = "not-a-valid-email";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", email))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenEmptyEmail_whenCheckingEmailAvailability_thenReturnsBadRequest() throws Exception {
      String email = "";

      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability")
              .param("email", email))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingEmail_whenCheckingEmailAvailability_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .get(BASE_URL + "/email-availability"))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }
  }

  @Nested
  class CreateTests {
    @Test
    void givenValidData_whenCreatingResearcher_thenReturnsCreatedResearcher() throws Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "john@example.com");

      UUID id = UUID.randomUUID();
      CreateResearcherResponseDTO response = new CreateResearcherResponseDTO(
          id,
          dto.name(),
          dto.email());

      Mockito.when(researcherService.create(dto)).thenReturn(response);

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpectAll(
              MockMvcResultMatchers.status().isCreated(),
              MockMvcResultMatchers.jsonPath("$.id").value(response.id().toString()),
              MockMvcResultMatchers.jsonPath("$.name").value(response.name()),
              MockMvcResultMatchers.jsonPath("$.email").value(response.email()));

      Mockito.verify(researcherService).create(dto);
    }

    @Test
    void givenMalformedJson_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content("""
                  {
                    "name": "John",
                    "email": "john@example.com"
                  """)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingBodyContent_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenEmptyName_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "",
          "john@example.com");

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingName_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content("""
                  {
                    "email": "john@example.com"
                  }
                  """)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void given256CharacterName_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "a".repeat(256),
          "john@example.com");

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenEmptyEmail_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "");

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenMissingEmail_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content("""
                  {
                    "name": "John Doe"
                  }
                  """)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }

    @Test
    void givenInvalidEmail_whenCreatingResearcher_thenReturnsBadRequest() throws Exception {
      CreateResearcherRequestDTO dto = new CreateResearcherRequestDTO(
          "John Doe",
          "not-a-valid-email");

      mockMvc.perform(
          MockMvcRequestBuilders
              .post(BASE_URL)
              .content(objectMapper.writeValueAsString(dto))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }
  }

  @Nested
  class DeleteTests {
    @Test
    void givenExistingId_whenDeletingResearcher_thenReturnsNoContent() throws Exception {
      UUID id = UUID.randomUUID();

      mockMvc.perform(
          MockMvcRequestBuilders
              .delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNoContent());

      Mockito.verify(researcherService).delete(id);
    }

    @Test
    void givenNonExistingId_whenDeletingResearcher_thenReturnsNotFound() throws Exception {
      UUID id = UUID.randomUUID();

      Mockito.doThrow(new ResearcherNotFoundException(id)).when(researcherService).delete(id);

      mockMvc.perform(
          MockMvcRequestBuilders
              .delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isNotFound());

      Mockito.verify(researcherService).delete(id);
    }

    @Test
    void givenInvalidId_whenDeletingResearcher_thenReturnsBadRequest() throws Exception {
      String id = "not-a-valid-Id";

      mockMvc.perform(
          MockMvcRequestBuilders
              .delete(BASE_URL + "/{id}", id))
          .andExpect(MockMvcResultMatchers.status().isBadRequest());

      Mockito.verifyNoInteractions(researcherService);
    }
  }

}
