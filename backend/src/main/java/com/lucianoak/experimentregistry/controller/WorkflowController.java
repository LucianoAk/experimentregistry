package com.lucianoak.experimentregistry.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucianoak.experimentregistry.dto.workflow.request.CreateWorkflowRequestDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindAllWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.service.WorkflowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<CreateWorkflowResponseDTO> create(@RequestBody @Valid CreateWorkflowRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workflowService.create(dto));
    }
    
    @GetMapping
    public ResponseEntity<List<FindAllWorkflowResponseDTO>> findAll() {
        return ResponseEntity.ok(workflowService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindWorkflowResponseDTO> findById(@PathVariable @Valid UUID id) {
        return ResponseEntity.ok(workflowService.findById(id));
    }
    
}
