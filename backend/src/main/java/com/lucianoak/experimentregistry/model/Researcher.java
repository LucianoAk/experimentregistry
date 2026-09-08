package com.lucianoak.experimentregistry.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "researchers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Researcher {

  @Builder
  private Researcher(String name, String email) {
    this.name = name;
    this.email = email;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "email", unique = true, length = 255)
  private String email;

  @OneToMany(mappedBy = "researcher", fetch = FetchType.LAZY)
  @Getter(value = AccessLevel.NONE)
  @Setter(value = AccessLevel.NONE)
  private List<Experiment> experiments = new ArrayList<>();

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public void addExperiment(Experiment experiment) {
    experiments.add(experiment);
    experiment.setResearcher(this);
  }

  public List<Experiment> getExperiments() {
    return List.copyOf(experiments);
  }
}
