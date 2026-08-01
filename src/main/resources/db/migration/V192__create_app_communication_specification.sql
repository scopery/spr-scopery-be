-- Communication Specification (design-time) + Function link
CREATE TABLE app_communication_specification (
    id                              UUID         PRIMARY KEY,
    application_id                  UUID         NOT NULL,
    workspace_id                    UUID         NOT NULL,
    code                            VARCHAR(50)  NOT NULL,
    name                            VARCHAR(255) NOT NULL,
    description                     TEXT,
    status                          VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    trigger_name                    VARCHAR(255),
    trigger_key                     VARCHAR(100),
    trigger_timing                  VARCHAR(50),
    condition_json                  JSONB,
    suppression_condition_json      JSONB,
    delivery_policy_json            JSONB,
    in_app_contract_json            JSONB,
    email_contract_json             JSONB,
    recipients_json                 JSONB,
    owner_id                        UUID,
    version                         INTEGER      NOT NULL DEFAULT 1,
    created_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by                      VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    archived_at                     TIMESTAMPTZ,
    CONSTRAINT uq_app_comm_spec_code UNIQUE (application_id, code)
);

CREATE INDEX idx_app_comm_spec_application ON app_communication_specification (application_id);
CREATE INDEX idx_app_comm_spec_status ON app_communication_specification (application_id, status);

CREATE TABLE app_function_communication (
    function_id       UUID         NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    communication_id  UUID         NOT NULL REFERENCES app_communication_specification(id) ON DELETE CASCADE,
    note              TEXT,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_function_communication PRIMARY KEY (function_id, communication_id)
);

CREATE INDEX idx_app_function_comm_fn ON app_function_communication (function_id);
CREATE INDEX idx_app_function_comm_comm ON app_function_communication (communication_id);
