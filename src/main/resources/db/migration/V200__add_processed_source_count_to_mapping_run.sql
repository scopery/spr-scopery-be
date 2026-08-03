-- Progress fields for async AI mapping runs (UI polls GET …/runs/{runId}).
ALTER TABLE ai_mapping_run
    ADD COLUMN IF NOT EXISTS processed_source_count INTEGER NOT NULL DEFAULT 0;
