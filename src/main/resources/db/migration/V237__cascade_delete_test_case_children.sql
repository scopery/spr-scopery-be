-- Fix: deleting a test case with steps or coverage links was failing with FK violation (500).
-- Add ON DELETE CASCADE so children are automatically removed when the parent is deleted.

ALTER TABLE quality_test_case_step
    DROP CONSTRAINT IF EXISTS quality_test_case_step_test_case_id_fkey,
    ADD CONSTRAINT quality_test_case_step_test_case_id_fkey
        FOREIGN KEY (test_case_id) REFERENCES quality_test_case(id) ON DELETE CASCADE;

ALTER TABLE quality_test_case_coverage
    DROP CONSTRAINT IF EXISTS fk_quality_test_case_coverage_quality_test_case,
    ADD CONSTRAINT fk_quality_test_case_coverage_quality_test_case
        FOREIGN KEY (test_case_id) REFERENCES quality_test_case(id) ON DELETE CASCADE;
