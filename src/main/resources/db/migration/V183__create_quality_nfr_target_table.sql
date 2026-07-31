CREATE TABLE quality_nfr_target (
    id              UUID          PRIMARY KEY,
    requirement_id  UUID          NOT NULL REFERENCES requirements_requirement(id),
    target_type     VARCHAR(50)   NOT NULL,
    target_id       UUID,
    target_label    VARCHAR(255),
    display_order   INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL
);
CREATE INDEX idx_quality_nfr_target_requirement_id ON quality_nfr_target(requirement_id);
