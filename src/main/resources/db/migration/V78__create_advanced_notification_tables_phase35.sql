-- Phase 35: Advanced notifications (digest/reminder/alert/preferences/subscriptions)
CREATE TABLE IF NOT EXISTS notification_preference_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID NOT NULL, timezone VARCHAR(100),
    default_mode VARCHAR(50), digest_enabled BOOLEAN NOT NULL DEFAULT FALSE, quiet_hours_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    quiet_hours_start VARCHAR(10), quiet_hours_end VARCHAR(10), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_preference_profile PRIMARY KEY (id),
    CONSTRAINT uq_notification_preference_profile UNIQUE (workspace_id, user_id)
);
CREATE TABLE IF NOT EXISTS notification_subscription (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID NOT NULL, target_type VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    subscription_level VARCHAR(50) NOT NULL, auto_subscribed BOOLEAN NOT NULL DEFAULT FALSE, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_subscription PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_notification_subscription_active ON notification_subscription(user_id, target_type, target_id) WHERE status = 'ACTIVE';
CREATE TABLE IF NOT EXISTS notification_digest_rule (
    id UUID NOT NULL, workspace_id UUID NOT NULL, code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    scope VARCHAR(50), frequency VARCHAR(50) NOT NULL, schedule_config_json TEXT, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_digest_rule PRIMARY KEY (id), CONSTRAINT uq_notification_digest_rule UNIQUE (workspace_id, code)
);
CREATE TABLE IF NOT EXISTS notification_reminder_rule (
    id UUID NOT NULL, workspace_id UUID NOT NULL, rule_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    condition_json TEXT, recipient_rule_json TEXT, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_reminder_rule PRIMARY KEY (id), CONSTRAINT uq_notification_reminder_rule UNIQUE (workspace_id, rule_code)
);
CREATE TABLE IF NOT EXISTS notification_reminder_instance (
    id UUID NOT NULL, workspace_id UUID NOT NULL, reminder_rule_id UUID, source_type VARCHAR(50), source_id UUID,
    recipient_user_id UUID NOT NULL, remind_at TIMESTAMPTZ NOT NULL, status VARCHAR(50) NOT NULL, dedup_key VARCHAR(255),
    snoozed_until TIMESTAMPTZ, dismissed_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_reminder_instance PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS notification_alert_rule (
    id UUID NOT NULL, workspace_id UUID NOT NULL, rule_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    category VARCHAR(50), condition_json TEXT, bypass_quiet_hours BOOLEAN NOT NULL DEFAULT FALSE, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_alert_rule PRIMARY KEY (id), CONSTRAINT uq_notification_alert_rule UNIQUE (workspace_id, rule_code)
);
CREATE TABLE IF NOT EXISTS notification_alert_event (
    id UUID NOT NULL, workspace_id UUID NOT NULL, alert_rule_id UUID, source_type VARCHAR(50), source_id UUID,
    severity VARCHAR(50), title VARCHAR(500), status VARCHAR(50) NOT NULL, dedup_key VARCHAR(255),
    acknowledged_at TIMESTAMPTZ, dismissed_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_notification_alert_event PRIMARY KEY (id)
);
