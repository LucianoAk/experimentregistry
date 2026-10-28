package com.lucianoak.experimentregistry.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.repository.ExperimentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ExperimentIntregrationTest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ExperimentRepository experimentRepository;

  private static final String BASE_URL = "/api/experiments";

  @AfterEach
  void tearDown() {
    experimentRepository.deleteAll();
  }

}
