package com.lucianoak.experimentregistry.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucianoak.experimentregistry.model.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, UUID> {
    
}
