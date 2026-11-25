package com.lucianoak.experimentregistry.repository;

import com.lucianoak.experimentregistry.model.Workflow;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {

  List<Workflow> findAllByActiveTrueOrderByVersionDesc();

  Optional<Workflow> findTopByOrderByVersionDesc();
}
