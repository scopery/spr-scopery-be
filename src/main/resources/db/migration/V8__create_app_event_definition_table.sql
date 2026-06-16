CREATE TABLE app_event_definition
(
    id            UUID         NOT NULL,
    code          VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    event_key     VARCHAR(150) NOT NULL,
    description   TEXT,
    input_schema  TEXT,
    output_schema TEXT,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT pk_app_event_definition PRIMARY KEY (id),
    CONSTRAINT uq_app_event_definition_code UNIQUE (code),
    CONSTRAINT uq_app_event_definition_source_system_event_key UNIQUE (source_system, event_key)
);

CREATE INDEX idx_app_event_definition_code          ON app_event_definition (code);
CREATE INDEX idx_app_event_definition_source_system ON app_event_definition (source_system);
CREATE INDEX idx_app_event_definition_event_key     ON app_event_definition (event_key);
CREATE INDEX idx_app_event_definition_status        ON app_event_definition (status);