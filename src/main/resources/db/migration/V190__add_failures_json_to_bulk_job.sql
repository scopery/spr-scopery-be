ALTER TABLE app_bulk_job
    ADD COLUMN IF NOT EXISTS failures_json TEXT;
