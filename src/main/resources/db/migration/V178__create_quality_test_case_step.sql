CREATE TABLE IF NOT EXISTS quality_test_case_step (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_case_id    UUID NOT NULL REFERENCES quality_test_case(id),
    project_id      UUID NOT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    action          TEXT NOT NULL,
    expected_result TEXT,
    screen_id       UUID,
    component_id    UUID,
    archived_at     TIMESTAMPTZ,
    archived_by     UUID,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_quality_test_case_step_tc ON quality_test_case_step(test_case_id);
CREATE INDEX IF NOT EXISTS idx_quality_test_case_step_project ON quality_test_case_step(project_id);
