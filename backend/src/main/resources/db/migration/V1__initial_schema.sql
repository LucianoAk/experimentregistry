-- Researchers
CREATE TABLE researchers (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE researchers ADD CONSTRAINT pk_researchers PRIMARY KEY (id);

COMMENT ON TABLE researchers IS 'The researcher responsible for an experiment';
COMMENT ON COLUMN researchers.id IS 'Primary key, the internal database ID for a researcher';
COMMENT ON COLUMN researchers.name IS 'The name of the researcher';
COMMENT ON COLUMN researchers.email IS 'The email of the researcher';
COMMENT ON COLUMN researchers.created_at IS 'The creation date of the entry';

-- Experiment statuses
CREATE TABLE experiment_statuses (
    id UUID NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    sequence_order INTEGER NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE experiment_statuses ADD CONSTRAINT pk_experiment_statuses PRIMARY KEY (id);
ALTER TABLE experiment_statuses ADD CONSTRAINT chk_experiment_sequence_order CHECK (sequence_order >= 1);

COMMENT ON TABLE experiment_statuses IS 'The status for experiments';
COMMENT ON COLUMN experiment_statuses.id IS 'Primary key, the internal database ID for an experiment status';
COMMENT ON COLUMN experiment_statuses.name IS 'The text value of the status';
COMMENT ON COLUMN experiment_statuses.sequence_order IS 'The position of the status in the sequence';
COMMENT ON COLUMN experiment_statuses.created_at IS 'The creation date of the entry';

-- Experiments
CREATE TABLE experiments (
    id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE,
    finish_date TIMESTAMP WITH TIME ZONE,
    result TEXT,
    status_id UUID NOT NULL,
    researcher_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE experiments ADD CONSTRAINT pk_experiments PRIMARY KEY (id);
ALTER TABLE experiments ADD CONSTRAINT fk_experiments_status FOREIGN KEY (status_id) REFERENCES experiment_statuses(id);
ALTER TABLE experiments ADD CONSTRAINT fk_experiments_researcher FOREIGN KEY (researcher_id) REFERENCES researchers(id);

COMMENT ON TABLE experiments IS 'The experiment in the registry';
COMMENT ON COLUMN experiments.id IS 'Primary key, the internal database ID for the experiment';
COMMENT ON COLUMN experiments.title IS 'The title describing the experiment';
COMMENT ON COLUMN experiments.start_date IS 'The date and time that the experiment starts';
COMMENT ON COLUMN experiments.finish_date IS 'The date and time that the experiment finishes';
COMMENT ON COLUMN experiments.result IS 'The result of the experiment';
COMMENT ON COLUMN experiments.status_id IS 'Foreign key referencing the experiment_statuses table, the current status of the experiment';
COMMENT ON COLUMN experiments.researcher_id IS 'Foreign key referencing the researchers table, the researcher responsible for the experiment';
COMMENT ON COLUMN experiments.created_at IS 'The creation date of the entry';

-- Parameters
CREATE TABLE parameters (
    id UUID NOT NULL,
    experiment_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    measurement NUMERIC(19, 6) NOT NULL,
    unit VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE parameters ADD CONSTRAINT pk_parameters PRIMARY KEY (id);
ALTER TABLE parameters ADD CONSTRAINT fk_parameters_experiment FOREIGN KEY (experiment_id) REFERENCES experiments(id) ON DELETE CASCADE;
ALTER TABLE parameters ADD CONSTRAINT uq_parameters_experiment_name UNIQUE (experiment_id, name);

COMMENT ON TABLE parameters IS 'The parameters of an experiment';
COMMENT ON COLUMN parameters.id IS 'Primary key, the internal database ID for a parameter';
COMMENT ON COLUMN parameters.experiment_id IS 'Foreign key referencing the experiments table, the experiment to which this parameter belongs';
COMMENT ON COLUMN parameters.name IS 'The descriptive name of the parameter';
COMMENT ON COLUMN parameters.measurement IS 'The value of the parameter';
COMMENT ON COLUMN parameters.unit IS 'The unit of the parameter';
COMMENT ON COLUMN parameters.description IS 'Text with more details about the parameter';
COMMENT ON COLUMN parameters.created_at IS 'The creation date of the entry';