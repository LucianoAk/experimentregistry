package com.lucianoak.experimentregistry.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "experiments")
@Getter
@Setter
@NoArgsConstructor
public class Experiment {

    @Builder
    public Experiment(String title, Instant startDate, Instant finishDate, String result, ExperimentStatus status, Researcher researcher, List<Parameter> parameters) {
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.result = result;
        this.status = status;
        this.researcher = researcher;
        this.parameters = parameters;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "start_date")
    private Instant startDate;
    
    @Column(name = "finish_date")
    private Instant finishDate;
    
    @Column(name = "result")
    private String result;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private ExperimentStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "researcher_id", nullable = false)
    private Researcher researcher;

    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parameter> parameters = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}