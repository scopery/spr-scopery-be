-- Phase 3: AC ↔ Test Case join for criterion-level coverage
CREATE TABLE quality_test_case_acceptance_criterion (
    id                       UUID         NOT NULL,
    project_id               UUID         NOT NULL,
    test_case_id             UUID         NOT NULL REFERENCES quality_test_case(id) ON DELETE CASCADE,
    acceptance_criterion_id  UUID         NOT NULL REFERENCES app_use_case_acceptance_criterion(id) ON DELETE CASCADE,
    created_at               TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by               VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_quality_tc_ac PRIMARY KEY (id),
    CONSTRAINT uq_quality_tc_ac UNIQUE (test_case_id, acceptance_criterion_id)
);

CREATE INDEX idx_quality_tc_ac_project ON quality_test_case_acceptance_criterion(project_id);
CREATE INDEX idx_quality_tc_ac_tc ON quality_test_case_acceptance_criterion(test_case_id);
CREATE INDEX idx_quality_tc_ac_ac ON quality_test_case_acceptance_criterion(acceptance_criterion_id);
