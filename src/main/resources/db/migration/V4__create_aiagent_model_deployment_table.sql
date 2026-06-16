CREATE TABLE aiagent_model_deployment
(
    id                       UUID           NOT NULL,
    model_id                 UUID           NOT NULL,
    name                     VARCHAR(255)   NOT NULL,
    code                     VARCHAR(100)   NOT NULL,
    environment              VARCHAR(50)    NOT NULL,
    provider_deployment_id   VARCHAR(255)   NOT NULL,
    endpoint_url             VARCHAR(500),
    default_temperature      NUMERIC(4, 2),
    default_max_output_tokens INTEGER,
    is_default               BOOLEAN        NOT NULL DEFAULT FALSE,
    description              TEXT,
    status                   VARCHAR(50)    NOT NULL,
    created_at               TIMESTAMP      NOT NULL,
    updated_at               TIMESTAMP      NOT NULL,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100),

    CONSTRAINT pk_aiagent_model_deployment PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_model_deployment_aiagent_model
        FOREIGN KEY (model_id) REFERENCES aiagent_model (id),
    CONSTRAINT uq_aiagent_model_deployment_model_id_code
        UNIQUE (model_id, code),
    CONSTRAINT ck_aiagent_model_deployment_default_temperature_range
        CHECK (default_temperature IS NULL OR default_temperature BETWEEN 0 AND 2),
    CONSTRAINT ck_aiagent_model_deployment_default_max_output_tokens
        CHECK (default_max_output_tokens IS NULL OR default_max_output_tokens > 0)
);

CREATE UNIQUE INDEX uq_aiagent_model_deployment_default_per_model_env
    ON aiagent_model_deployment (model_id, environment)
    WHERE is_default = TRUE;

CREATE INDEX idx_aiagent_model_deployment_model_id              ON aiagent_model_deployment (model_id);
CREATE INDEX idx_aiagent_model_deployment_environment           ON aiagent_model_deployment (environment);
CREATE INDEX idx_aiagent_model_deployment_status               ON aiagent_model_deployment (status);
CREATE INDEX idx_aiagent_model_deployment_code                 ON aiagent_model_deployment (code);
CREATE INDEX idx_aiagent_model_deployment_provider_deployment_id ON aiagent_model_deployment (provider_deployment_id);
CREATE INDEX idx_aiagent_model_deployment_is_default           ON aiagent_model_deployment (is_default);
