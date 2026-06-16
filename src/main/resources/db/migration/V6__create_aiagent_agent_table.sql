CREATE TABLE aiagent_agent
(
    id                          UUID           NOT NULL,
    name                        VARCHAR(255)   NOT NULL,
    code                        VARCHAR(100)   NOT NULL,
    type                        VARCHAR(50)    NOT NULL,
    description                 TEXT,
    default_model_deployment_id UUID,
    output_format               VARCHAR(50),
    status                      VARCHAR(50)    NOT NULL,
    created_at                  TIMESTAMP      NOT NULL,
    updated_at                  TIMESTAMP      NOT NULL,
    created_by                  VARCHAR(100),
    updated_by                  VARCHAR(100),

    CONSTRAINT pk_aiagent_agent
        PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_agent_aiagent_model_deployment
        FOREIGN KEY (default_model_deployment_id) REFERENCES aiagent_model_deployment (id),
    CONSTRAINT uq_aiagent_agent_code
        UNIQUE (code)
);

CREATE INDEX idx_aiagent_agent_code
    ON aiagent_agent (code);
CREATE INDEX idx_aiagent_agent_type
    ON aiagent_agent (type);
CREATE INDEX idx_aiagent_agent_status
    ON aiagent_agent (status);
CREATE INDEX idx_aiagent_agent_output_format
    ON aiagent_agent (output_format);
CREATE INDEX idx_aiagent_agent_default_model_deployment_id
    ON aiagent_agent (default_model_deployment_id);
