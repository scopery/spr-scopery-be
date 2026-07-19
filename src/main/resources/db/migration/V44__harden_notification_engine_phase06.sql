-- Phase 06: harden notification engine (mandatory/sensitive rules, outbox dedup, in-app items)

-- ── Email rule: mandatory + sensitive variable allowance ─────────────────────
ALTER TABLE notification_email_rule
    ADD COLUMN IF NOT EXISTS mandatory BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE notification_email_rule
    ADD COLUMN IF NOT EXISTS allow_sensitive_variables BOOLEAN NOT NULL DEFAULT false;

-- ── Email outbox: dedup_key + provider_message_id ────────────────────────────
ALTER TABLE notification_email_outbox
    ADD COLUMN IF NOT EXISTS dedup_key VARCHAR(255);

ALTER TABLE notification_email_outbox
    ADD COLUMN IF NOT EXISTS provider_message_id VARCHAR(255);

UPDATE notification_email_outbox
SET dedup_key = id::text
WHERE dedup_key IS NULL;

ALTER TABLE notification_email_outbox
    ALTER COLUMN dedup_key SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_notification_email_outbox_dedup_key'
    ) THEN
        ALTER TABLE notification_email_outbox
            ADD CONSTRAINT uq_notification_email_outbox_dedup_key UNIQUE (dedup_key);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_notification_email_outbox_dedup_key
    ON notification_email_outbox (dedup_key);

-- Expand outbox status check if present (PENDING/RETRY_SCHEDULED/DEAD_LETTER)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_notification_email_outbox_status'
    ) THEN
        ALTER TABLE notification_email_outbox DROP CONSTRAINT chk_notification_email_outbox_status;
    END IF;
END $$;

ALTER TABLE notification_email_outbox
    ADD CONSTRAINT chk_notification_email_outbox_status
        CHECK (status IN (
            'PENDING', 'PROCESSING', 'SENT', 'FAILED', 'CANCELLED', 'SKIPPED',
            'RETRY_SCHEDULED', 'DEAD_LETTER'
        ));

-- ── In-app notification_item ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notification_item
(
    id                   UUID         NOT NULL,
    recipient_user_id    UUID         NOT NULL,
    event_definition_id  UUID,
    source_system        VARCHAR(100),
    source_resource_type VARCHAR(100),
    source_resource_id   UUID,
    organization_id      UUID,
    workspace_id         UUID,
    project_id           UUID,
    title                VARCHAR(255) NOT NULL,
    body_preview         TEXT,
    severity             VARCHAR(50)  NOT NULL,
    priority             VARCHAR(50)  NOT NULL DEFAULT 'NORMAL',
    action_type          VARCHAR(100),
    action_url           TEXT,
    dedup_key            VARCHAR(255) NOT NULL,
    mandatory            BOOLEAN      NOT NULL DEFAULT false,
    status               VARCHAR(50)  NOT NULL,
    read_at              TIMESTAMP,
    dismissed_at         TIMESTAMP,
    trace_id             VARCHAR(100),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_notification_item PRIMARY KEY (id),
    CONSTRAINT uq_notification_item_recipient_dedup UNIQUE (recipient_user_id, dedup_key),
    CONSTRAINT chk_notification_item_status CHECK (status IN ('UNREAD', 'READ', 'DISMISSED', 'ARCHIVED')),
    CONSTRAINT chk_notification_item_severity CHECK (severity IN (
        'INFO', 'SUCCESS', 'WARNING', 'ERROR', 'SECURITY', 'APPROVAL'
    )),
    CONSTRAINT chk_notification_item_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
);

CREATE INDEX IF NOT EXISTS idx_notification_item_recipient_user_id
    ON notification_item (recipient_user_id);
CREATE INDEX IF NOT EXISTS idx_notification_item_status
    ON notification_item (status);
CREATE INDEX IF NOT EXISTS idx_notification_item_created_at
    ON notification_item (created_at);
