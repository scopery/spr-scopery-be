-- Phase 02: IAM user security / identity hardening fields
ALTER TABLE iam_user
    ADD COLUMN IF NOT EXISTS registration_source VARCHAR(50) NOT NULL DEFAULT 'SELF_SIGNUP',
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS password_reset_required BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS failed_login_count INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS last_login_failed_at TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE iam_user
    DROP CONSTRAINT IF EXISTS ck_iam_user_registration_source;

ALTER TABLE iam_user
    ADD CONSTRAINT ck_iam_user_registration_source
        CHECK (registration_source IN ('SELF_SIGNUP', 'INVITATION', 'ADMIN_PROVISIONED', 'SYSTEM_BOOTSTRAP'));

CREATE INDEX IF NOT EXISTS idx_iam_user_last_login_at ON iam_user (last_login_at);
