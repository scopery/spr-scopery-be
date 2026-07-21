CREATE TABLE aiassistant_workspace_config (
    id                          UUID            NOT NULL DEFAULT gen_random_uuid(),
    workspace_id                UUID            NOT NULL,
    model_deployment_id         UUID,
    model_provider              VARCHAR(100),
    model_name                  VARCHAR(200),
    system_prompt_override      TEXT,
    temperature_override        NUMERIC(4,2),
    max_output_tokens_override  INT,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by                  VARCHAR(255)    NOT NULL DEFAULT 'SYSTEM',
    updated_by                  VARCHAR(255)    NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_aiassistant_workspace_config PRIMARY KEY (id),
    CONSTRAINT uq_aiassistant_workspace_config_workspace UNIQUE (workspace_id)
);

CREATE INDEX idx_aiassistant_workspace_config_workspace
    ON aiassistant_workspace_config(workspace_id);
