ALTER TABLE quality_defect ADD COLUMN source_verification_result_id UUID;
ALTER TABLE quality_defect ADD COLUMN source_test_run_id UUID;
ALTER TABLE quality_defect ADD COLUMN source_test_case_id UUID;
ALTER TABLE quality_defect ADD COLUMN source_verification_case_id UUID;

CREATE INDEX idx_quality_defect_source_test_run_id ON quality_defect(source_test_run_id) WHERE source_test_run_id IS NOT NULL;
CREATE INDEX idx_quality_defect_source_test_case_id ON quality_defect(source_test_case_id) WHERE source_test_case_id IS NOT NULL;
CREATE INDEX idx_quality_defect_source_verification_result_id ON quality_defect(source_verification_result_id) WHERE source_verification_result_id IS NOT NULL;
