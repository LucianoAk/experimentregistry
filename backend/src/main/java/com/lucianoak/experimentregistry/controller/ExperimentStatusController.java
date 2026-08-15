package com.lucianoak.experimentregistry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experimentstatus.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentStatusService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/experiments/status")
@RequiredArgsConstructor
public class ExperimentStatusController {
    
    private final ExperimentStatusService experimentStatusService;

    @GetMapping
    public ResponseEntity<List<FindExperimentStatusResponseDTO>> findAll() {
        return ResponseEntity.ok(experimentStatusService.findAll());
    }
    
}
