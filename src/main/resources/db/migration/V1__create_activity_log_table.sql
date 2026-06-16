CREATE TABLE app_activity_log
(
    id          UUID         NOT NULL PRIMARY KEY,
    module_code VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   VARCHAR(100),
    action      VARCHAR(100) NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    actor_id    VARCHAR(100),
    actor_name  VARCHAR(255),
    trace_id    VARCHAR(100),
    message     TEXT,
    metadata    JSONB,
    created_at  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_activity_log_module_code ON app_activity_log (module_code);
CREATE INDEX idx_activity_log_entity_type ON app_activity_log (entity_type);
CREATE INDEX idx_activity_log_entity_id   ON app_activity_log (entity_id);
CREATE INDEX idx_activity_log_action      ON app_activity_log (action);
CREATE INDEX idx_activity_log_created_at  ON app_activity_log (created_at);
CREATE INDEX idx_activity_log_trace_id    ON app_activity_log (trace_id);
