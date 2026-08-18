package com.lucianoak.experimentregistry.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.service.WorkflowService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<CreateWorkflowResponseDTO> create() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workflowService.create());
    }
    
    @GetMapping
    public ResponseEntity<List<FindWorkflowResponseDTO>> findAll() {
        return ResponseEntity.ok(workflowService.findAll());
    }
    
}
