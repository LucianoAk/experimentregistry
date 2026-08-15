package com.lucianoak.experimentregistry.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucianoak.experimentregistry.model.ExperimentStatus;

public interface ExperimentStatusRepository extends JpaRepository<ExperimentStatus, UUID> {

    List<ExperimentStatus> findAllByOrderBySequenceOrderAsc();
    
}
