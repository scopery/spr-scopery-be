-- Fix: MappingSummaryJpaEntity extends AuditableJpaEntity but V197 omitted audit columns.
-- Without these, every summary INSERT fails and mapping generate completes with 0 suggestions.
ALTER TABLE ai_mapping_summary
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
