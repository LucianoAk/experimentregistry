package com.lucianoak.experimentregistry.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.lucianoak.experimentregistry.exception.DuplicateStatusException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "experiment_workflows")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Workflow {

    @Builder
    private Workflow(Integer version) {
        this.version = version;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "version", nullable = false, unique = true)
    private Integer version;

    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private List<ExperimentStatus> statuses = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void addStatus(ExperimentStatus status) {
        boolean alreadyExists = statuses.stream()
                .anyMatch(existing -> existing.getName().equals(status.getName()));

        if (alreadyExists) {
            throw new DuplicateStatusException(status.getName());
        }
        status.setSequenceOrder(statuses.size() + 1);
        statuses.add(status);
        status.setWorkflow(this);
    }

    public List<ExperimentStatus> getStatuses() {
        return List.copyOf(statuses);
    }

}
