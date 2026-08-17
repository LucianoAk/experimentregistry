package com.lucianoak.experimentregistry.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucianoak.experimentregistry.model.Experiment;
import com.lucianoak.experimentregistry.model.Parameter;
import java.util.List;

public interface ParameterRepository extends JpaRepository<Parameter, UUID> {

    List<Parameter> findByExperiment(Experiment experiment);

    boolean existsByExperimentAndName(Experiment experiment, String name);

}
