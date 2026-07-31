CREATE TABLE quality_verification_case (
    id                  UUID         PRIMARY KEY,
    project_id          UUID         NOT NULL,
    requirement_id      UUID         NOT NULL REFERENCES requirements_requirement(id),
    code                VARCHAR(50),
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    verification_method VARCHAR(50)  NOT NULL,
    procedure           TEXT,
    expected_result_json TEXT,
    environment         VARCHAR(100),
    lifecycle_status    VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
    automation_status   VARCHAR(20)  NOT NULL DEFAULT 'MANUAL',
    owner_id            UUID,
    assignee_id         UUID,
    archived_at         TIMESTAMPTZ,
    archived_by         UUID,
    version             INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255)
);
CREATE INDEX idx_quality_verification_case_project_id    ON quality_verification_case(project_id);
CREATE INDEX idx_quality_verification_case_requirement_id ON quality_verification_case(requirement_id);
