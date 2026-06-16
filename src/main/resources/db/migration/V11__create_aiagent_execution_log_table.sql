CREATE TABLE aiagent_execution_log
(
    id                   UUID           NOT NULL,
    request_id           VARCHAR(150)   NOT NULL,
    event_config_id      UUID,
    event_definition_id  UUID,
    agent_id             UUID           NOT NULL,
    prompt_version_id    UUID           NOT NULL,
    model_deployment_id  UUID           NOT NULL,
    trigger_source       VARCHAR(50)    NOT NULL,
    status               VARCHAR(50)    NOT NULL,
    started_at           TIMESTAMP,
    completed_at         TIMESTAMP,
    latency_ms           BIGINT,
    input_token_count    INTEGER,
    output_token_count   INTEGER,
    total_token_count    INTEGER,
    estimated_cost       NUMERIC(14, 4),
    provider_request_id  VARCHAR(255),
    error_code           VARCHAR(150),
    error_message        TEXT,
    metadata             TEXT,
    created_at           TIMESTAMP      NOT NULL,
    updated_at           TIMESTAMP      NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_aiagent_execution_log
        PRIMARY KEY (id),

    CONSTRAINT uq_aiagent_execution_log_request_id
        UNIQUE (request_id),

    CONSTRAINT fk_aiagent_execution_log_aiagent_event_config
        FOREIGN KEY (event_config_id) REFERENCES aiagent_event_config (id),

    CONSTRAINT fk_aiagent_execution_log_app_event_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id),

    CONSTRAINT fk_aiagent_execution_log_aiagent_agent
        FOREIGN KEY (agent_id) REFERENCES aiagent_agent (id),

    CONSTRAINT fk_aiagent_execution_log_aiagent_prompt_version
        FOREIGN KEY (prompt_version_id) REFERENCES aiagent_prompt_version (id),

    CONSTRAINT fk_aiagent_execution_log_aiagent_model_deployment
        FOREIGN KEY (model_deployment_id) REFERENCES aiagent_model_deployment (id),

    CONSTRAINT ck_aiagent_execution_log_input_tokens_non_negative
        CHECK (input_token_count IS NULL OR input_token_count >= 0),

    CONSTRAINT ck_aiagent_execution_log_output_tokens_non_negative
        CHECK (output_token_count IS NULL OR output_token_count >= 0),

    CONSTRAINT ck_aiagent_execution_log_total_tokens_non_negative
        CHECK (total_token_count IS NULL OR total_token_count >= 0),

    CONSTRAINT ck_aiagent_execution_log_estimated_cost_non_negative
        CHECK (estimated_cost IS NULL OR estimated_cost >= 0)
);

CREATE INDEX idx_aiagent_execution_log_request_id         ON aiagent_execution_log (request_id);
CREATE INDEX idx_aiagent_execution_log_event_config_id    ON aiagent_execution_log (event_config_id);
CREATE INDEX idx_aiagent_execution_log_event_definition_id ON aiagent_execution_log (event_definition_id);
CREATE INDEX idx_aiagent_execution_log_agent_id           ON aiagent_execution_log (agent_id);
CREATE INDEX idx_aiagent_execution_log_prompt_version_id  ON aiagent_execution_log (prompt_version_id);
CREATE INDEX idx_aiagent_execution_log_model_deployment_id ON aiagent_execution_log (model_deployment_id);
CREATE INDEX idx_aiagent_execution_log_trigger_source     ON aiagent_execution_log (trigger_source);
CREATE INDEX idx_aiagent_execution_log_status             ON aiagent_execution_log (status);
CREATE INDEX idx_aiagent_execution_log_created_at         ON aiagent_execution_log (created_at);
CREATE INDEX idx_aiagent_execution_log_completed_at       ON aiagent_execution_log (completed_at);