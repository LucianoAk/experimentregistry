package com.lucianoak.experimentregistry.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lucianoak.experimentregistry.configuration.JpaConfig;

@DataJpaTest
@Testcontainers
@Import(JpaConfig.class)
public class ExperimentRepositoryTest {

  @Container
  @ServiceConnection
  private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

  @Autowired
  private ExperimentRepository experimentRepository;

  @Autowired
  private TestEntityManager entityManager;
}
