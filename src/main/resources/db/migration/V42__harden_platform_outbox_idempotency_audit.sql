-- Phase 04: harden transactional outbox, idempotency, and append-only audit.

-- ---------------------------------------------------------------------------
-- Transactional outbox: processor lock / retry / dead-letter fields
-- ---------------------------------------------------------------------------
ALTER TABLE app_transactional_outbox
    ADD COLUMN IF NOT EXISTS event_version INT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS source_system VARCHAR(100) NOT NULL DEFAULT 'SCOPERY',
    ADD COLUMN IF NOT EXISTS available_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS max_attempts INT NOT NULL DEFAULT 10,
    ADD COLUMN IF NOT EXISTS last_attempt_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS next_retry_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS locked_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP,
    ADD COLUMN IF NOT EXISTS error_code VARCHAR(150),
    ADD COLUMN IF NOT EXISTS error_message TEXT,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE app_transactional_outbox
    DROP CONSTRAINT IF EXISTS ck_app_transactional_outbox_status;

ALTER TABLE app_transactional_outbox
    ADD CONSTRAINT ck_app_transactional_outbox_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'PUBLISHED', 'FAILED', 'DEAD_LETTER', 'CANCELLED'));

DROP INDEX IF EXISTS idx_app_transactional_outbox_pending;
CREATE INDEX idx_app_transactional_outbox_claim
    ON app_transactional_outbox (status, available_at, next_retry_at, locked_until);

-- ---------------------------------------------------------------------------
-- Idempotency: request hash + lifecycle status
-- ---------------------------------------------------------------------------
ALTER TABLE app_idempotency_key
    ADD COLUMN IF NOT EXISTS request_hash VARCHAR(64),
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE app_idempotency_key
    ALTER COLUMN response_status DROP NOT NULL;

ALTER TABLE app_idempotency_key
    ALTER COLUMN response_body DROP NOT NULL;

UPDATE app_idempotency_key
SET request_hash = COALESCE(request_hash, key_hash)
WHERE request_hash IS NULL;

ALTER TABLE app_idempotency_key
    ALTER COLUMN request_hash SET NOT NULL;

ALTER TABLE app_idempotency_key
    DROP CONSTRAINT IF EXISTS ck_app_idempotency_key_status;

ALTER TABLE app_idempotency_key
    ADD CONSTRAINT ck_app_idempotency_key_status
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'EXPIRED'));

-- ---------------------------------------------------------------------------
-- Audit: severity + append-only enforcement
-- ---------------------------------------------------------------------------
ALTER TABLE app_audit_event
    ADD COLUMN IF NOT EXISTS severity VARCHAR(50) NOT NULL DEFAULT 'INFO';

ALTER TABLE app_audit_event
    DROP CONSTRAINT IF EXISTS ck_app_audit_event_severity;

ALTER TABLE app_audit_event
    ADD CONSTRAINT ck_app_audit_event_severity
        CHECK (severity IN ('INFO', 'WARNING', 'SECURITY', 'COMPLIANCE', 'CRITICAL'));

CREATE OR REPLACE FUNCTION fn_app_audit_event_append_only()
RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'app_audit_event is append-only';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_app_audit_event_no_update ON app_audit_event;
CREATE TRIGGER trg_app_audit_event_no_update
    BEFORE UPDATE OR DELETE ON app_audit_event
    FOR EACH ROW EXECUTE PROCEDURE fn_app_audit_event_append_only();
