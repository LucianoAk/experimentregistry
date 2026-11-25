package com.lucianoak.experimentregistry.model;

import com.lucianoak.experimentregistry.exception.DuplicateStatusException;
import com.lucianoak.experimentregistry.exception.ExperimentStatusHasAssociatedExperimentsException;
import com.lucianoak.experimentregistry.exception.StatusNotAssociatedWithWorkflowException;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

  @OneToMany(
      mappedBy = "workflow",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Getter(value = AccessLevel.NONE)
  @Setter(value = AccessLevel.NONE)
  private List<ExperimentStatus> statuses = new ArrayList<>();

  @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY)
  @Getter(value = AccessLevel.NONE)
  @Setter(value = AccessLevel.NONE)
  private List<Experiment> experiments = new ArrayList<>();

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public List<ExperimentStatus> getStatuses() {
    return List.copyOf(statuses);
  }

  public void addStatus(ExperimentStatus status, Integer position) {
    boolean alreadyExists =
        statuses.stream().anyMatch(existing -> existing.getName().equals(status.getName()));

    if (alreadyExists) {
      throw new DuplicateStatusException(status.getName());
    }

    statuses.sort(Comparator.comparing(ExperimentStatus::getSequenceOrder));

    int targetPosition = (position == null) ? statuses.size() : position;

    if (targetPosition < 0 || targetPosition > statuses.size()) {
      throw new InvalidEndpointRequestException(
          "Invalid experiment status request", "Sequence order out of bounds: " + targetPosition);
    }

    statuses.add(targetPosition, status);
    status.setWorkflow(this);

    for (int i = 0; i < statuses.size(); i++) {
      statuses.get(i).setSequenceOrder(i);
    }
  }

  public void addStatus(ExperimentStatus status) {
    addStatus(status, null);
  }

  public void removeStatus(UUID statusId) {
    ExperimentStatus status =
        statuses.stream()
            .filter(s -> s.getId().equals(statusId))
            .findFirst()
            .orElseThrow(
                () -> new StatusNotAssociatedWithWorkflowException(statusId, this.getId()));

    if (!status.getExperiments().isEmpty()) {
      throw new ExperimentStatusHasAssociatedExperimentsException(statusId);
    }

    statuses.remove(status);

    statuses.sort(Comparator.comparing(ExperimentStatus::getSequenceOrder));

    for (int i = 0; i < statuses.size(); i++) {
      statuses.get(i).setSequenceOrder(i);
    }
  }

  public List<Experiment> getExperiments() {
    return List.copyOf(experiments);
  }

  public void addExperiment(Experiment experiment) {
    experiments.add(experiment);
    experiment.setWorkflow(this);
  }
}
