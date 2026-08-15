package com.lucianoak.experimentregistry.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucianoak.experimentregistry.model.Experiment;

public interface ExperimentRepository extends JpaRepository<Experiment, UUID> {

    List<Experiment> findAllByOrderByTitleAsc();
    
    List<Experiment> findAllByOrderByStatusSequenceOrderAsc();

}
