CREATE TABLE resourceref_resource_type (
    id              UUID            NOT NULL,
    code            VARCHAR(128)    NOT NULL,
    display_name    VARCHAR(255)    NOT NULL,
    description     VARCHAR(1000)   NULL,
    is_system       BOOLEAN         NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_resourceref_resource_type PRIMARY KEY (id),
    CONSTRAINT uq_resourceref_resource_type_code UNIQUE (code)
);

CREATE INDEX idx_resourceref_resource_type_enabled ON resourceref_resource_type (enabled);
