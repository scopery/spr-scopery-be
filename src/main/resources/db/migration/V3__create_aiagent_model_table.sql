CREATE TABLE aiagent_model
(
    id                UUID         NOT NULL,
    provider_id       UUID         NOT NULL,
    name              VARCHAR(255) NOT NULL,
    code              VARCHAR(100) NOT NULL,
    provider_model_id VARCHAR(255) NOT NULL,
    type              VARCHAR(50)  NOT NULL,
    description       TEXT,
    status            VARCHAR(50)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT pk_aiagent_model PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_model_aiagent_provider
        FOREIGN KEY (provider_id) REFERENCES aiagent_provider (id),
    CONSTRAINT uq_aiagent_model_provider_id_code
        UNIQUE (provider_id, code)
);

CREATE INDEX idx_aiagent_model_provider_id       ON aiagent_model (provider_id);
CREATE INDEX idx_aiagent_model_status            ON aiagent_model (status);
CREATE INDEX idx_aiagent_model_type              ON aiagent_model (type);
CREATE INDEX idx_aiagent_model_code              ON aiagent_model (code);
CREATE INDEX idx_aiagent_model_provider_model_id ON aiagent_model (provider_model_id);
