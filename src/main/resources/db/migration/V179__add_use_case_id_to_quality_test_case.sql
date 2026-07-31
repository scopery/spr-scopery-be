ALTER TABLE quality_test_case ADD COLUMN use_case_id UUID REFERENCES app_use_case(id);
CREATE INDEX idx_quality_test_case_use_case_id ON quality_test_case(use_case_id);
