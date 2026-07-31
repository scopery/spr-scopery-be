CREATE TABLE quality_run_membership (
    id              UUID                        DEFAULT gen_random_uuid() NOT NULL,
    project_id      UUID                        NOT NULL,
    test_run_id     UUID                        NOT NULL,
    case_kind       VARCHAR(20)                 NOT NULL,
    case_id         UUID                        NOT NULL,
    display_order   INTEGER                     NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_quality_run_membership PRIMARY KEY (id),
    CONSTRAINT uq_quality_run_membership_case UNIQUE (test_run_id, case_kind, case_id),
    CONSTRAINT fk_quality_run_membership_test_run FOREIGN KEY (test_run_id) REFERENCES quality_test_run(id) ON DELETE CASCADE,
    CONSTRAINT chk_quality_run_membership_kind CHECK (case_kind IN ('FUNCTIONAL', 'NFR'))
);

CREATE INDEX idx_quality_run_membership_test_run_id ON quality_run_membership(test_run_id);
CREATE INDEX idx_quality_run_membership_project_id  ON quality_run_membership(project_id);
