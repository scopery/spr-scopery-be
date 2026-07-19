-- Phase 35: digest run history table for tracking dispatched digest batches
CREATE TABLE IF NOT EXISTS notification_digest_run (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    digest_rule_id UUID,
    recipient_user_id UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    notification_count INT NOT NULL DEFAULT 0,
    sent_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_notification_digest_run PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_notification_digest_run_workspace
    ON notification_digest_run (workspace_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notification_digest_run_recipient
    ON notification_digest_run (workspace_id, recipient_user_id, created_at DESC);
