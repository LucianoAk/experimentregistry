package com.lucianoak.experimentregistry.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
public class ExperimentE2ETest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  @LocalServerPort
  private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    this.restClient = RestClient.builder()
        .baseUrl("http://localhost:" + port + "/api/experiments")
        .build();
  }

}
