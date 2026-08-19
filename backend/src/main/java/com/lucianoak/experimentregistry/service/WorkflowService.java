package com.lucianoak.experimentregistry.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.CreateExperimentStatuesResponseDTO;
import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.request.CreateWorkflowRequestDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.CreateWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindAllWorkflowResponseDTO;
import com.lucianoak.experimentregistry.dto.workflow.response.FindWorkflowResponseDTO;
import com.lucianoak.experimentregistry.exception.WorkflowNotFountException;
import com.lucianoak.experimentregistry.model.ExperimentStatus;
import com.lucianoak.experimentregistry.model.Workflow;
import com.lucianoak.experimentregistry.repository.WorkflowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Transactional
    public CreateWorkflowResponseDTO create(CreateWorkflowRequestDTO dto) {
        Integer nextVersion = workflowRepository.findTopByOrderByVersionDesc()
                .map(Workflow::getVersion)
                .orElse(0) + 1;

        Workflow workflow = Workflow.builder()
                .version(nextVersion)
                .build();

        dto.statuses().forEach(s -> workflow.addStatus(
                ExperimentStatus.builder()
                        .name(s.name())
                        .build()));

        workflowRepository.save(workflow);

        return new CreateWorkflowResponseDTO(
                workflow.getId(),
                workflow.getVersion(),
                workflow.getStatuses().stream()
                        .map(s -> new CreateExperimentStatuesResponseDTO(
                                s.getId(),
                                s.getName(),
                                s.getSequenceOrder()))
                        .toList());
    }

    public List<FindAllWorkflowResponseDTO> findAll() {
        return workflowRepository.findAllByOrderByVersionDesc().stream()
                .map(w -> new FindAllWorkflowResponseDTO(
                        w.getId(),
                        w.getVersion(),
                        w.getCreatedAt()))
                .toList();
    }

    public FindWorkflowResponseDTO findById(UUID id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFountException(id));
                
        return new FindWorkflowResponseDTO(
            workflow.getId(),
            workflow.getVersion(),
            workflow.getStatuses().stream()
                    .map(s -> new FindExperimentStatusResponseDTO(
                            s.getId(),
                            s.getName(),
                            s.getSequenceOrder()))
                    .toList(),
            workflow.getCreatedAt()
        );
    }

}
