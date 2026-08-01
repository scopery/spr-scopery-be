ALTER TABLE app_use_case
    ALTER COLUMN primary_function_id DROP NOT NULL;

DROP INDEX IF EXISTS idx_app_use_case_function;
CREATE INDEX idx_app_use_case_function ON app_use_case (primary_function_id)
    WHERE primary_function_id IS NOT NULL;
