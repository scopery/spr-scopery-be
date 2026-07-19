-- Phase 20: Project notification subscriptions, preferences, reminders

CREATE TABLE IF NOT EXISTS project_notification_subscription (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    subscriber_user_id UUID NOT NULL,
    workspace_member_id UUID NOT NULL,
    subscription_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_notification_subscription PRIMARY KEY (id),
    CONSTRAINT fk_project_notification_subscription_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT ck_project_notification_subscription_status CHECK (status IN ('ACTIVE','MUTED','ARCHIVED')),
    CONSTRAINT ck_project_notification_subscription_type CHECK (subscription_type IN (
        'PROJECT_WATCHER','PROJECT_OWNER','PROJECT_MANAGER','FINANCE_WATCHER',
        'QUOTE_WATCHER','CHANGE_WATCHER','BASELINE_WATCHER'))
);
CREATE INDEX IF NOT EXISTS idx_project_notification_subscription_project
    ON project_notification_subscription(project_id);
CREATE INDEX IF NOT EXISTS idx_project_notification_subscription_user
    ON project_notification_subscription(subscriber_user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_notification_subscription_active
    ON project_notification_subscription(project_id, subscriber_user_id, subscription_type)
    WHERE status = 'ACTIVE';

CREATE TABLE IF NOT EXISTS project_task_notification_subscription (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    task_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    subscriber_user_id UUID NOT NULL,
    workspace_member_id UUID NOT NULL,
    subscription_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_task_notification_subscription PRIMARY KEY (id),
    CONSTRAINT fk_project_task_notification_subscription_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT ck_project_task_notification_subscription_status CHECK (status IN ('ACTIVE','MUTED','ARCHIVED')),
    CONSTRAINT ck_project_task_notification_subscription_type CHECK (subscription_type IN (
        'TASK_WATCHER','TASK_ASSIGNEE_AUTO','TASK_IN_CHARGE_AUTO'))
);
CREATE INDEX IF NOT EXISTS idx_project_task_notification_subscription_task
    ON project_task_notification_subscription(task_id);
CREATE INDEX IF NOT EXISTS idx_project_task_notification_subscription_project
    ON project_task_notification_subscription(project_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_task_notification_subscription_active
    ON project_task_notification_subscription(task_id, subscriber_user_id, subscription_type)
    WHERE status = 'ACTIVE';

CREATE TABLE IF NOT EXISTS project_notification_preference (
    id UUID NOT NULL,
    project_id UUID,
    task_id UUID,
    workspace_id UUID NOT NULL,
    user_id UUID NOT NULL,
    event_code VARCHAR(150),
    channel VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    muted BOOLEAN NOT NULL DEFAULT FALSE,
    mandatory_override BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_notification_preference PRIMARY KEY (id),
    CONSTRAINT ck_project_notification_preference_channel CHECK (channel IN ('IN_APP','EMAIL'))
);
CREATE INDEX IF NOT EXISTS idx_project_notification_preference_user
    ON project_notification_preference(user_id, workspace_id);
CREATE INDEX IF NOT EXISTS idx_project_notification_preference_project
    ON project_notification_preference(project_id);

CREATE TABLE IF NOT EXISTS project_reminder_run (
    id UUID NOT NULL,
    workspace_id UUID,
    run_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    result_summary_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    trace_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_reminder_run PRIMARY KEY (id),
    CONSTRAINT ck_project_reminder_run_type CHECK (run_type IN (
        'TASK_DUE_SOON','TASK_OVERDUE','MILESTONE_DUE_SOON','MILESTONE_MISSED','COMBINED_DAILY')),
    CONSTRAINT ck_project_reminder_run_status CHECK (status IN ('RUNNING','COMPLETED','FAILED'))
);
CREATE INDEX IF NOT EXISTS idx_project_reminder_run_started ON project_reminder_run(started_at);

CREATE TABLE IF NOT EXISTS project_reminder_emission (
    id UUID NOT NULL,
    reminder_run_id UUID,
    project_id UUID NOT NULL,
    task_id UUID,
    milestone_id UUID,
    recipient_user_id UUID NOT NULL,
    reminder_type VARCHAR(50) NOT NULL,
    reminder_date DATE NOT NULL,
    dedup_key VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_reminder_emission PRIMARY KEY (id),
    CONSTRAINT fk_project_reminder_emission_run FOREIGN KEY (reminder_run_id)
        REFERENCES project_reminder_run(id),
    CONSTRAINT uq_project_reminder_emission_dedup UNIQUE (dedup_key),
    CONSTRAINT ck_project_reminder_emission_status CHECK (status IN ('EMITTED','SKIPPED','FAILED')),
    CONSTRAINT ck_project_reminder_emission_type CHECK (reminder_type IN (
        'TASK_DUE_SOON','TASK_OVERDUE','MILESTONE_DUE_SOON','MILESTONE_MISSED'))
);
CREATE INDEX IF NOT EXISTS idx_project_reminder_emission_task ON project_reminder_emission(task_id);
