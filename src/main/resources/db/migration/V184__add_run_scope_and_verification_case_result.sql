ALTER TABLE quality_test_run ADD COLUMN run_scope VARCHAR(50) NOT NULL DEFAULT 'FUNCTIONAL';

CREATE TABLE quality_verification_case_result (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    test_run_id UUID NOT NULL REFERENCES quality_test_run(id),
    verification_case_id UUID NOT NULL REFERENCES quality_verification_case(id),
    result_status VARCHAR(50) NOT NULL DEFAULT 'NOT_RUN',
    actual_value DECIMAL(20,4),
    actual_value_unit VARCHAR(50),
    actual_result_json TEXT,
    evidence_reference TEXT,
    executed_at TIMESTAMPTZ,
    executed_by_id UUID,
    defect_id UUID,
    comment TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
CREATE INDEX idx_quality_vcr_test_run_id ON quality_verification_case_result(test_run_id);
CREATE INDEX idx_quality_vcr_verification_case_id ON quality_verification_case_result(verification_case_id);
