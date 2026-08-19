package com.lucianoak.experimentregistry.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lucianoak.experimentregistry.dto.experimentstatus.response.FindExperimentStatusResponseDTO;
import com.lucianoak.experimentregistry.repository.ExperimentStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentStatusService {

    private final ExperimentStatusRepository experimentStatusRepository;

    public List<FindExperimentStatusResponseDTO> searchByWorkflow(UUID workflowId) {
        return experimentStatusRepository.findAllByWorkflowIdOrderBySequenceOrder(workflowId).stream()
        .map(s -> new FindExperimentStatusResponseDTO(
            s.getId(),
            s.getName(),
            s.getSequenceOrder()
        )).toList();
    }
    
}