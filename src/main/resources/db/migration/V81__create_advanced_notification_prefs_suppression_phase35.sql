-- Phase 35 follow-up: category/channel preferences + suppression ledger
CREATE TABLE IF NOT EXISTS notification_channel_preference (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    user_id UUID NOT NULL,
    category_code VARCHAR(100) NOT NULL,
    channel_code VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_notification_channel_preference PRIMARY KEY (id),
    CONSTRAINT uq_notification_channel_preference UNIQUE (workspace_id, user_id, category_code, channel_code)
);
CREATE INDEX IF NOT EXISTS idx_notification_channel_preference_user
    ON notification_channel_preference (workspace_id, user_id);

CREATE TABLE IF NOT EXISTS notification_suppression_ledger (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID,
    user_id UUID,
    category_code VARCHAR(100),
    channel_code VARCHAR(50),
    reason_code VARCHAR(100) NOT NULL,
    source_type VARCHAR(100),
    source_id UUID,
    suppressed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_notification_suppression_ledger PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_notification_suppression_user
    ON notification_suppression_ledger (workspace_id, user_id, suppressed_at DESC);
