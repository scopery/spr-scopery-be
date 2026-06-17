ALTER TABLE notification_email_rule
    ADD COLUMN IF NOT EXISTS name        VARCHAR(255),
    ADD COLUMN IF NOT EXISTS description TEXT;
