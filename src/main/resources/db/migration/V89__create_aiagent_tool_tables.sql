-- AIG-012 AiTool registry foundation (governed tool catalog)

CREATE TABLE aiagent_tool
(
    id                       UUID          NOT NULL,
    code                     VARCHAR(100)  NOT NULL,
    name                     VARCHAR(255)  NOT NULL,
    description              TEXT,
    category                 VARCHAR(100)  NOT NULL,
    mutation_type            VARCHAR(50)   NOT NULL,
    requires_human_approval  BOOLEAN       NOT NULL DEFAULT FALSE,
    status                   VARCHAR(50)   NOT NULL,
    created_at               TIMESTAMP     NOT NULL,
    updated_at               TIMESTAMP     NOT NULL,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100),

    CONSTRAINT pk_aiagent_tool PRIMARY KEY (id),
    CONSTRAINT uq_aiagent_tool_code UNIQUE (code),
    CONSTRAINT chk_aiagent_tool_mutation_type
        CHECK (mutation_type IN ('READ', 'MUTATION')),
    CONSTRAINT chk_aiagent_tool_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'DEPRECATED'))
);

CREATE INDEX idx_aiagent_tool_code ON aiagent_tool (code);
CREATE INDEX idx_aiagent_tool_category ON aiagent_tool (category);
CREATE INDEX idx_aiagent_tool_status ON aiagent_tool (status);

CREATE TABLE aiagent_tool_permission
(
    id               UUID         NOT NULL,
    tool_id          UUID         NOT NULL,
    permission_code  VARCHAR(150) NOT NULL,
    description      TEXT,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),

    CONSTRAINT pk_aiagent_tool_permission PRIMARY KEY (id),
    CONSTRAINT uq_aiagent_tool_permission_tool_code UNIQUE (tool_id, permission_code),
    CONSTRAINT fk_aiagent_tool_permission_aiagent_tool
        FOREIGN KEY (tool_id) REFERENCES aiagent_tool (id)
);

CREATE INDEX idx_aiagent_tool_permission_tool_id ON aiagent_tool_permission (tool_id);
CREATE INDEX idx_aiagent_tool_permission_permission_code ON aiagent_tool_permission (permission_code);

CREATE TABLE aiagent_agent_tool_binding
(
    id          UUID        NOT NULL,
    agent_id    UUID        NOT NULL,
    tool_id     UUID        NOT NULL,
    status      VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP   NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT pk_aiagent_agent_tool_binding PRIMARY KEY (id),
    CONSTRAINT uq_aiagent_agent_tool_binding_agent_tool UNIQUE (agent_id, tool_id),
    CONSTRAINT fk_aiagent_agent_tool_binding_aiagent_tool
        FOREIGN KEY (tool_id) REFERENCES aiagent_tool (id),
    CONSTRAINT chk_aiagent_agent_tool_binding_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_aiagent_agent_tool_binding_agent_id ON aiagent_agent_tool_binding (agent_id);
CREATE INDEX idx_aiagent_agent_tool_binding_tool_id ON aiagent_agent_tool_binding (tool_id);
CREATE INDEX idx_aiagent_agent_tool_binding_status ON aiagent_agent_tool_binding (status);

CREATE TABLE aiagent_tool_execution
(
    id                   UUID         NOT NULL,
    tool_id              UUID         NOT NULL,
    agent_id             UUID,
    requested_by_user_id UUID,
    status               VARCHAR(50)  NOT NULL,
    approval_state       VARCHAR(50)  NOT NULL,
    input_summary        VARCHAR(1000),
    error_message        VARCHAR(2000),
    result_summary       VARCHAR(2000),
    started_at           TIMESTAMP,
    finished_at          TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_aiagent_tool_execution PRIMARY KEY (id),
    CONSTRAINT fk_aiagent_tool_execution_aiagent_tool
        FOREIGN KEY (tool_id) REFERENCES aiagent_tool (id),
    CONSTRAINT chk_aiagent_tool_execution_status
        CHECK (status IN ('PENDING', 'RUNNING', 'SUCCEEDED', 'FAILED', 'DENIED', 'NO_OP')),
    CONSTRAINT chk_aiagent_tool_execution_approval
        CHECK (approval_state IN ('NOT_REQUIRED', 'PENDING', 'APPROVED', 'REJECTED', 'BYPASSED_POLICY'))
);

CREATE INDEX idx_aiagent_tool_execution_tool_id ON aiagent_tool_execution (tool_id);
CREATE INDEX idx_aiagent_tool_execution_agent_id ON aiagent_tool_execution (agent_id);
CREATE INDEX idx_aiagent_tool_execution_status ON aiagent_tool_execution (status);
CREATE INDEX idx_aiagent_tool_execution_created_at ON aiagent_tool_execution (created_at);
