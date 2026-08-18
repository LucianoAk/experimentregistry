package com.lucianoak.experimentregistry.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentStatusService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/experiment-statuses")
@RequiredArgsConstructor
public class ExperimentStatusController {
    
    private final ExperimentStatusService experimentStatusService;

    @GetMapping
    public ResponseEntity<List<FindExperimentStatusResponseDTO>> findAllInSequence() {
        return ResponseEntity.ok(experimentStatusService.findAllInSequence());
    }
    
}
