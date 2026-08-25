package com.lucianoak.experimentregistry.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.parameter.request.CreateParameterRequestDTO;
import com.lucianoak.experimentregistry.dto.parameter.response.CreateParameterResponseDTO;
import com.lucianoak.experimentregistry.service.ParameterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
public class ParameterController {

  private final ParameterService parameterService;

  @PostMapping
  public ResponseEntity<CreateParameterResponseDTO> create(@RequestBody @Valid CreateParameterRequestDTO dto) {
    return ResponseEntity.ok(parameterService.create(dto));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    return ResponseEntity.noContent().build();
  }
}
