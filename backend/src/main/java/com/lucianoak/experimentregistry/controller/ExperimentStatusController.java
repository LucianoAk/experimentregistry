package com.lucianoak.experimentregistry.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.service.ExperimentStatusService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/experiment-statuses")
@RequiredArgsConstructor
public class ExperimentStatusController {
    
    private final ExperimentStatusService experimentStatusService;

    @GetMapping
    public ResponseEntity<List<FindExperimentStatusResponseDTO>> getMethodName(@RequestParam UUID workflowId) {
        return ResponseEntity.ok(experimentStatusService.searchByWorkflow(workflowId));
    }
    
    
}
