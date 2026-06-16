CREATE TABLE aiagent_provider
(
    id           UUID         NOT NULL,
    name         VARCHAR(255) NOT NULL,
    code         VARCHAR(100) NOT NULL,
    type         VARCHAR(100) NOT NULL,
    api_base_url VARCHAR(500),
    description  TEXT,
    status       VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_aiagent_provider    PRIMARY KEY (id),
    CONSTRAINT uq_aiagent_provider_code UNIQUE (code)
);

CREATE INDEX idx_aiagent_provider_status ON aiagent_provider (status);
CREATE INDEX idx_aiagent_provider_type   ON aiagent_provider (type);
CREATE INDEX idx_aiagent_provider_code   ON aiagent_provider (code);
