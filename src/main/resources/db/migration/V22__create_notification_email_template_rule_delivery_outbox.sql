-- ============================================================
-- Notification: Email Template
-- ============================================================
CREATE TABLE notification_email_template
(
    id                  UUID         NOT NULL,
    workspace_id        UUID,
    template_code       VARCHAR(150) NOT NULL,
    event_definition_id UUID         NOT NULL,
    locale              VARCHAR(20)  NOT NULL DEFAULT 'en',
    name                VARCHAR(255) NOT NULL,
    description         TEXT,
    scope               VARCHAR(50)  NOT NULL,
    current_version_id  UUID,
    status              VARCHAR(50)  NOT NULL,
    deleted_at          TIMESTAMP,
    deleted_by          UUID,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    CONSTRAINT pk_notification_email_template PRIMARY KEY (id),
    CONSTRAINT fk_notification_email_template_event_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id)
);

CREATE INDEX idx_notification_email_template_workspace_id        ON notification_email_template (workspace_id);
CREATE INDEX idx_notification_email_template_template_code       ON notification_email_template (template_code);
CREATE INDEX idx_notification_email_template_event_definition_id ON notification_email_template (event_definition_id);
CREATE INDEX idx_notification_email_template_scope               ON notification_email_template (scope);
CREATE INDEX idx_notification_email_template_status              ON notification_email_template (status);
CREATE INDEX idx_notification_email_template_deleted_at          ON notification_email_template (deleted_at);

-- System templates: unique code+locale when not deleted and workspace is null
CREATE UNIQUE INDEX uq_notification_email_template_system_active
    ON notification_email_template (template_code, locale)
    WHERE workspace_id IS NULL AND deleted_at IS NULL;

-- Workspace templates: unique workspace+code+locale when not deleted
CREATE UNIQUE INDEX uq_notification_email_template_workspace_active
    ON notification_email_template (workspace_id, template_code, locale)
    WHERE workspace_id IS NOT NULL AND deleted_at IS NULL;

-- ============================================================
-- Notification: Email Template Version
-- ============================================================
CREATE TABLE notification_email_template_version
(
    id                UUID        NOT NULL,
    template_id       UUID        NOT NULL,
    version_number    INTEGER     NOT NULL,
    subject_template  TEXT        NOT NULL,
    html_body_template TEXT,
    text_body_template TEXT,
    status            VARCHAR(50) NOT NULL,
    published_at      TIMESTAMP,
    published_by      UUID,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT pk_notification_email_template_version PRIMARY KEY (id),
    CONSTRAINT fk_notification_email_template_version_template
        FOREIGN KEY (template_id) REFERENCES notification_email_template (id),
    CONSTRAINT uq_notification_email_template_version_number
        UNIQUE (template_id, version_number)
);

CREATE INDEX idx_notification_email_template_version_template_id  ON notification_email_template_version (template_id);
CREATE INDEX idx_notification_email_template_version_status       ON notification_email_template_version (status);
CREATE INDEX idx_notification_email_template_version_published_at ON notification_email_template_version (published_at);

-- ============================================================
-- Notification: Email Rule
-- ============================================================
CREATE TABLE notification_email_rule
(
    id                   UUID         NOT NULL,
    workspace_id         UUID,
    rule_code            VARCHAR(150) NOT NULL,
    event_definition_id  UUID         NOT NULL,
    template_id          UUID         NOT NULL,
    recipient_strategy   VARCHAR(100) NOT NULL,
    recipient_config_json JSONB,
    scope                VARCHAR(50)  NOT NULL,
    priority             INTEGER      NOT NULL DEFAULT 100,
    enabled              BOOLEAN      NOT NULL DEFAULT true,
    status               VARCHAR(50)  NOT NULL,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_notification_email_rule PRIMARY KEY (id),
    CONSTRAINT fk_notification_email_rule_event_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id),
    CONSTRAINT fk_notification_email_rule_template
        FOREIGN KEY (template_id) REFERENCES notification_email_template (id)
);

CREATE INDEX idx_notification_email_rule_workspace_id        ON notification_email_rule (workspace_id);
CREATE INDEX idx_notification_email_rule_rule_code           ON notification_email_rule (rule_code);
CREATE INDEX idx_notification_email_rule_event_definition_id ON notification_email_rule (event_definition_id);
CREATE INDEX idx_notification_email_rule_template_id         ON notification_email_rule (template_id);
CREATE INDEX idx_notification_email_rule_recipient_strategy  ON notification_email_rule (recipient_strategy);
CREATE INDEX idx_notification_email_rule_enabled             ON notification_email_rule (enabled);
CREATE INDEX idx_notification_email_rule_status              ON notification_email_rule (status);
CREATE INDEX idx_notification_email_rule_deleted_at          ON notification_email_rule (deleted_at);
CREATE INDEX idx_notification_email_rule_priority            ON notification_email_rule (priority);

CREATE UNIQUE INDEX uq_notification_email_rule_system_active
    ON notification_email_rule (rule_code)
    WHERE workspace_id IS NULL AND deleted_at IS NULL;

CREATE UNIQUE INDEX uq_notification_email_rule_workspace_active
    ON notification_email_rule (workspace_id, rule_code)
    WHERE workspace_id IS NOT NULL AND deleted_at IS NULL;

-- ============================================================
-- Notification: Email Delivery
-- ============================================================
CREATE TABLE notification_email_delivery
(
    id                   UUID         NOT NULL,
    event_definition_id  UUID         NOT NULL,
    rule_id              UUID,
    workspace_id         UUID,
    actor_user_id        UUID,
    recipient_user_id    UUID,
    recipient_email      VARCHAR(255),
    template_id          UUID,
    template_version_id  UUID,
    subject_rendered     TEXT,
    html_body_rendered   TEXT,
    text_body_rendered   TEXT,
    payload_json         JSONB,
    trace_id             VARCHAR(100),
    status               VARCHAR(50)  NOT NULL,
    failure_reason       TEXT,
    sent_at              TIMESTAMP,
    failed_at            TIMESTAMP,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_notification_email_delivery PRIMARY KEY (id),
    CONSTRAINT fk_notification_email_delivery_event_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id),
    CONSTRAINT fk_notification_email_delivery_rule
        FOREIGN KEY (rule_id) REFERENCES notification_email_rule (id),
    CONSTRAINT fk_notification_email_delivery_template
        FOREIGN KEY (template_id) REFERENCES notification_email_template (id),
    CONSTRAINT fk_notification_email_delivery_template_version
        FOREIGN KEY (template_version_id) REFERENCES notification_email_template_version (id)
);

CREATE INDEX idx_notification_email_delivery_event_definition_id ON notification_email_delivery (event_definition_id);
CREATE INDEX idx_notification_email_delivery_rule_id             ON notification_email_delivery (rule_id);
CREATE INDEX idx_notification_email_delivery_workspace_id        ON notification_email_delivery (workspace_id);
CREATE INDEX idx_notification_email_delivery_actor_user_id       ON notification_email_delivery (actor_user_id);
CREATE INDEX idx_notification_email_delivery_recipient_user_id   ON notification_email_delivery (recipient_user_id);
CREATE INDEX idx_notification_email_delivery_recipient_email     ON notification_email_delivery (recipient_email);
CREATE INDEX idx_notification_email_delivery_status              ON notification_email_delivery (status);
CREATE INDEX idx_notification_email_delivery_trace_id            ON notification_email_delivery (trace_id);
CREATE INDEX idx_notification_email_delivery_created_at          ON notification_email_delivery (created_at);

-- ============================================================
-- Notification: Email Outbox
-- ============================================================
CREATE TABLE notification_email_outbox
(
    id             UUID         NOT NULL,
    delivery_id    UUID,
    to_email       VARCHAR(255) NOT NULL,
    subject        VARCHAR(500) NOT NULL,
    html_body      TEXT,
    text_body      TEXT,
    template_code  VARCHAR(150),
    provider       VARCHAR(50)  NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    max_retry      INTEGER      NOT NULL DEFAULT 3,
    next_retry_at  TIMESTAMP,
    sent_at        TIMESTAMP,
    failed_at      TIMESTAMP,
    failure_reason TEXT,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),

    CONSTRAINT pk_notification_email_outbox PRIMARY KEY (id),
    CONSTRAINT fk_notification_email_outbox_delivery
        FOREIGN KEY (delivery_id) REFERENCES notification_email_delivery (id)
);

CREATE INDEX idx_notification_email_outbox_delivery_id   ON notification_email_outbox (delivery_id);
CREATE INDEX idx_notification_email_outbox_to_email      ON notification_email_outbox (to_email);
CREATE INDEX idx_notification_email_outbox_status        ON notification_email_outbox (status);
CREATE INDEX idx_notification_email_outbox_provider      ON notification_email_outbox (provider);
CREATE INDEX idx_notification_email_outbox_next_retry_at ON notification_email_outbox (next_retry_at);
CREATE INDEX idx_notification_email_outbox_created_at    ON notification_email_outbox (created_at);
