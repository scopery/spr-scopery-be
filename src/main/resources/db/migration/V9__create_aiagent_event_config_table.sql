CREATE TABLE aiagent_event_config
(
    id                   UUID         NOT NULL,
    code                 VARCHAR(100) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    event_definition_id  UUID         NOT NULL,
    environment          VARCHAR(50)  NOT NULL,
    trigger_type         VARCHAR(50)  NOT NULL,
    agent_id             UUID         NOT NULL,
    prompt_version_id    UUID         NOT NULL,
    model_deployment_id  UUID         NOT NULL,
    condition_expression TEXT,
    description          TEXT,
    status               VARCHAR(50)  NOT NULL,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_aiagent_event_config
        PRIMARY KEY (id),

    CONSTRAINT uq_aiagent_event_config_code
        UNIQUE (code),

    CONSTRAINT fk_aiagent_event_config_app_event_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id),

    CONSTRAINT fk_aiagent_event_config_aiagent_agent
        FOREIGN KEY (agent_id) REFERENCES aiagent_agent (id),

    CONSTRAINT fk_aiagent_event_config_aiagent_prompt_version
        FOREIGN KEY (prompt_version_id) REFERENCES aiagent_prompt_version (id),

    CONSTRAINT fk_aiagent_event_config_aiagent_model_deployment
        FOREIGN KEY (model_deployment_id) REFERENCES aiagent_model_deployment (id)
);

CREATE UNIQUE INDEX uq_aiagent_event_config_active_event_env
    ON aiagent_event_config (event_definition_id, environment)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_aiagent_event_config_code               ON aiagent_event_config (code);
CREATE INDEX idx_aiagent_event_config_event_definition_id ON aiagent_event_config (event_definition_id);
CREATE INDEX idx_aiagent_event_config_environment         ON aiagent_event_config (environment);
CREATE INDEX idx_aiagent_event_config_trigger_type        ON aiagent_event_config (trigger_type);
CREATE INDEX idx_aiagent_event_config_status              ON aiagent_event_config (status);
CREATE INDEX idx_aiagent_event_config_agent_id            ON aiagent_event_config (agent_id);
CREATE INDEX idx_aiagent_event_config_prompt_version_id   ON aiagent_event_config (prompt_version_id);
CREATE INDEX idx_aiagent_event_config_model_deployment_id ON aiagent_event_config (model_deployment_id);
