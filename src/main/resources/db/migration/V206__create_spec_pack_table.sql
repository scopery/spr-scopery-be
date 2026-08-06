CREATE TABLE spec_pack (
    id                  UUID        NOT NULL,
    project_id          UUID        NOT NULL,
    pack_type           VARCHAR(50) NOT NULL,
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    status              VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    current_version_id  UUID,
    source_pack_id      UUID,
    created_by          VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL,
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMPTZ NOT NULL,
    archived_at         TIMESTAMPTZ,

    CONSTRAINT pk_spec_pack PRIMARY KEY (id)
);

CREATE INDEX idx_spec_pack_project_id ON spec_pack (project_id);
CREATE INDEX idx_spec_pack_status     ON spec_pack (status);
CREATE INDEX idx_spec_pack_pack_type  ON spec_pack (pack_type);
