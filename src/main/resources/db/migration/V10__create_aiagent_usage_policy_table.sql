CREATE TABLE aiagent_usage_policy
(
    id                       UUID           NOT NULL,
    code                     VARCHAR(100)   NOT NULL,
    name                     VARCHAR(255)   NOT NULL,
    target_type              VARCHAR(50)    NOT NULL,
    target_id                UUID,
    max_requests_per_period  INT,
    max_tokens_per_period    BIGINT,
    max_cost_per_period      NUMERIC(19, 6),
    max_concurrent_requests  INT,
    daily_budget             NUMERIC(19, 6),
    period                   VARCHAR(50),
    action                   VARCHAR(50)    NOT NULL,
    priority                 INT            NOT NULL DEFAULT 100,
    description              TEXT,
    status                   VARCHAR(50)    NOT NULL,
    created_at               TIMESTAMP      NOT NULL,
    updated_at               TIMESTAMP      NOT NULL,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100),

    CONSTRAINT pk_aiagent_usage_policy
        PRIMARY KEY (id),

    CONSTRAINT uq_aiagent_usage_policy_code
        UNIQUE (code),

    CONSTRAINT chk_aiagent_usage_policy_target_id
        CHECK (
            (target_type = 'GLOBAL' AND target_id IS NULL) OR
            (target_type <> 'GLOBAL' AND target_id IS NOT NULL)
        ),

    CONSTRAINT chk_aiagent_usage_policy_has_limit
        CHECK (
            max_requests_per_period IS NOT NULL OR
            max_tokens_per_period   IS NOT NULL OR
            max_cost_per_period     IS NOT NULL OR
            max_concurrent_requests IS NOT NULL OR
            daily_budget            IS NOT NULL
        ),

    CONSTRAINT chk_aiagent_usage_policy_period_required
        CHECK (
            (max_requests_per_period IS NULL AND max_tokens_per_period IS NULL AND max_cost_per_period IS NULL) OR
            period IS NOT NULL
        )
);

CREATE UNIQUE INDEX uq_aiagent_usage_policy_active_global
    ON aiagent_usage_policy (target_type)
    WHERE target_type = 'GLOBAL' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uq_aiagent_usage_policy_active_event_config
    ON aiagent_usage_policy (target_id)
    WHERE target_type = 'EVENT_CONFIG' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uq_aiagent_usage_policy_active_agent
    ON aiagent_usage_policy (target_id)
    WHERE target_type = 'AGENT' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uq_aiagent_usage_policy_active_model_deployment
    ON aiagent_usage_policy (target_id)
    WHERE target_type = 'MODEL_DEPLOYMENT' AND status = 'ACTIVE';

CREATE INDEX idx_aiagent_usage_policy_code        ON aiagent_usage_policy (code);
CREATE INDEX idx_aiagent_usage_policy_target_type ON aiagent_usage_policy (target_type);
CREATE INDEX idx_aiagent_usage_policy_target_id   ON aiagent_usage_policy (target_id);
CREATE INDEX idx_aiagent_usage_policy_status      ON aiagent_usage_policy (status);