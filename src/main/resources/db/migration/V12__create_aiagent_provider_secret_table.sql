CREATE TABLE aiagent_provider_secret (
    id              UUID        NOT NULL,
    provider_id     UUID        NOT NULL,
    secret_type     VARCHAR(50) NOT NULL,
    encrypted_value TEXT        NOT NULL,
    iv              VARCHAR(255) NOT NULL,
    key_version     VARCHAR(50) NOT NULL,
    masked_value    VARCHAR(100) NOT NULL,
    description     TEXT,
    status          VARCHAR(50) NOT NULL,
    last_rotated_at TIMESTAMP,
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP   NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT pk_aiagent_provider_secret PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_provider_secret_aiagent_provider
        FOREIGN KEY (provider_id) REFERENCES aiagent_provider (id)
);

-- Partial unique index: only one ACTIVE secret per provider + secret type
CREATE UNIQUE INDEX uq_aiagent_provider_secret_active_provider_type
    ON aiagent_provider_secret (provider_id, secret_type)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_aiagent_provider_secret_provider_id ON aiagent_provider_secret (provider_id);
CREATE INDEX idx_aiagent_provider_secret_secret_type ON aiagent_provider_secret (secret_type);
CREATE INDEX idx_aiagent_provider_secret_status      ON aiagent_provider_secret (status);
