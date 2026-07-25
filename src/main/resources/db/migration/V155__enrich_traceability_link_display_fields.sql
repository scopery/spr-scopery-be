-- §3.2: Snapshot source/target display fields on trace link (readable even after archiving)
ALTER TABLE traceability_link
    ADD COLUMN IF NOT EXISTS source_code  VARCHAR(200),
    ADD COLUMN IF NOT EXISTS source_title VARCHAR(500),
    ADD COLUMN IF NOT EXISTS target_code  VARCHAR(200),
    ADD COLUMN IF NOT EXISTS target_title VARCHAR(500);

-- Coverage matrix performance indexes
CREATE INDEX IF NOT EXISTS idx_traceability_link_source_type_link ON traceability_link(project_id, source_type, link_type, status);
CREATE INDEX IF NOT EXISTS idx_traceability_link_target_id        ON traceability_link(target_id, project_id);
CREATE INDEX IF NOT EXISTS idx_requirement_project_code           ON requirements_requirement(project_id, code);
CREATE INDEX IF NOT EXISTS idx_quality_test_case_result_tc        ON quality_test_case_result(test_case_id, project_id, executed_at DESC);
