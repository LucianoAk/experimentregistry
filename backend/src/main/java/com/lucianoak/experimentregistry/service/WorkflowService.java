package com.lucianoak.experimentregistry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.model.Workflow;
import com.lucianoak.experimentregistry.repository.WorkflowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Transactional
    public CreateWorkflowResponseDTO create() {
        Integer nextVersion = workflowRepository.findTopByOrderByVersionDesc()
                .map(Workflow::getVersion)
                .orElse(0) + 1;

        Workflow workflow = workflowRepository.save(
                Workflow.builder()
                        .version(nextVersion)
                        .build()
        );

        return new CreateWorkflowResponseDTO(
                workflow.getId(),
                workflow.getVersion()
        );
    }

    public List<FindWorkflowResponseDTO> findAll() {
        return workflowRepository.findAllByOrderByVersionDesc().stream()
                .map(w -> new FindWorkflowResponseDTO(
                        w.getId(),
                        w.getVersion(),
                        w.getCreatedAt()))
                .toList();
    }

}
