CREATE TABLE aiagent_model_parameter_capability
(
    id                UUID           NOT NULL,
    model_id          UUID           NOT NULL,
    parameter_name    VARCHAR(100)   NOT NULL,
    api_parameter_key VARCHAR(255),
    support_status    VARCHAR(50)    NOT NULL,
    value_type        VARCHAR(50)    NOT NULL,
    min_value         NUMERIC(12, 4),
    max_value         NUMERIC(12, 4),
    default_value     VARCHAR(500),
    nullable          BOOLEAN        NOT NULL DEFAULT TRUE,
    if_null_behavior  VARCHAR(100),
    description       TEXT,
    status            VARCHAR(50)    NOT NULL,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP      NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT pk_aiagent_model_parameter_capability
        PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_model_parameter_capability_aiagent_model
        FOREIGN KEY (model_id) REFERENCES aiagent_model (id),
    CONSTRAINT uq_aiagent_model_parameter_capability_model_id_parameter_name
        UNIQUE (model_id, parameter_name),
    CONSTRAINT ck_aiagent_model_parameter_capability_min_max
        CHECK (min_value IS NULL OR max_value IS NULL OR min_value <= max_value),
    CONSTRAINT ck_aiagent_model_parameter_capability_nullable_behavior
        CHECK (nullable = FALSE OR if_null_behavior IS NOT NULL)
);

CREATE INDEX idx_aiagent_model_parameter_capability_model_id
    ON aiagent_model_parameter_capability (model_id);
CREATE INDEX idx_aiagent_model_parameter_capability_parameter_name
    ON aiagent_model_parameter_capability (parameter_name);
CREATE INDEX idx_aiagent_model_parameter_capability_support_status
    ON aiagent_model_parameter_capability (support_status);
CREATE INDEX idx_aiagent_model_parameter_capability_value_type
    ON aiagent_model_parameter_capability (value_type);
CREATE INDEX idx_aiagent_model_parameter_capability_status
    ON aiagent_model_parameter_capability (status);
