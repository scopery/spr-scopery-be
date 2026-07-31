-- Many-to-many: Requirement (COVERED_BY) Use Case
CREATE TABLE app_requirement_use_case (
    requirement_id UUID         NOT NULL,
    use_case_id    UUID         NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_req_uc PRIMARY KEY (requirement_id, use_case_id)
);
CREATE INDEX idx_app_req_uc_req ON app_requirement_use_case(requirement_id);
CREATE INDEX idx_app_req_uc_uc  ON app_requirement_use_case(use_case_id);
