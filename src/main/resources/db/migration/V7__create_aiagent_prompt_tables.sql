CREATE TABLE aiagent_prompt_template
(
    id          UUID         NOT NULL,
    agent_id    UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    code        VARCHAR(100) NOT NULL,
    description TEXT,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT pk_aiagent_prompt_template
        PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_prompt_template_aiagent_agent
        FOREIGN KEY (agent_id) REFERENCES aiagent_agent (id),
    CONSTRAINT uq_aiagent_prompt_template_agent_id_code
        UNIQUE (agent_id, code)
);

CREATE INDEX idx_aiagent_prompt_template_agent_id ON aiagent_prompt_template (agent_id);
CREATE INDEX idx_aiagent_prompt_template_code     ON aiagent_prompt_template (code);
CREATE INDEX idx_aiagent_prompt_template_status   ON aiagent_prompt_template (status);

CREATE TABLE aiagent_prompt_version
(
    id              UUID         NOT NULL,
    template_id     UUID         NOT NULL,
    version_number  INTEGER      NOT NULL,
    title           VARCHAR(255),
    content         TEXT         NOT NULL,
    content_format  VARCHAR(50)  NOT NULL,
    variable_schema TEXT,
    change_note     TEXT,
    status          VARCHAR(50)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT pk_aiagent_prompt_version
        PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_prompt_version_aiagent_prompt_template
        FOREIGN KEY (template_id) REFERENCES aiagent_prompt_template (id),
    CONSTRAINT uq_aiagent_prompt_version_template_id_version_number
        UNIQUE (template_id, version_number)
);

CREATE UNIQUE INDEX uq_aiagent_prompt_version_active_per_template
    ON aiagent_prompt_version (template_id)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_aiagent_prompt_version_template_id    ON aiagent_prompt_version (template_id);
CREATE INDEX idx_aiagent_prompt_version_status         ON aiagent_prompt_version (status);
CREATE INDEX idx_aiagent_prompt_version_content_format ON aiagent_prompt_version (content_format);
CREATE INDEX idx_aiagent_prompt_version_version_number ON aiagent_prompt_version (version_number);
