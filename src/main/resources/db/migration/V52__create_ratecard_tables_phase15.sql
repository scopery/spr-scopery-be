-- Phase 15: Rate Card / Cost Policy foundation
-- Tables: rate_cost_role, rate_workspace_member_cost_role, rate_card,
--         rate_card_version, rate_card_line, rate_inflation_policy

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. rate_cost_role
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_cost_role (
    id                  UUID            NOT NULL,
    code                VARCHAR(100)    NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    scope               VARCHAR(50)     NOT NULL,
    organization_id     UUID,
    workspace_id        UUID,
    category            VARCHAR(100),
    built_in            BOOLEAN         NOT NULL DEFAULT FALSE,
    status              VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    archived_at         TIMESTAMPTZ,
    archived_by         UUID,
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_rate_cost_role PRIMARY KEY (id),
    CONSTRAINT ck_rate_cost_role_scope CHECK (scope IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE')),
    CONSTRAINT ck_rate_cost_role_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_rate_cost_role_scope_code
    ON rate_cost_role (
        scope,
        COALESCE(organization_id, '00000000-0000-0000-0000-000000000000'),
        COALESCE(workspace_id, '00000000-0000-0000-0000-000000000000'),
        code
    );

CREATE INDEX IF NOT EXISTS idx_rate_cost_role_scope ON rate_cost_role (scope);
CREATE INDEX IF NOT EXISTS idx_rate_cost_role_org ON rate_cost_role (organization_id);
CREATE INDEX IF NOT EXISTS idx_rate_cost_role_workspace ON rate_cost_role (workspace_id);
CREATE INDEX IF NOT EXISTS idx_rate_cost_role_status ON rate_cost_role (status);
CREATE INDEX IF NOT EXISTS idx_rate_cost_role_code ON rate_cost_role (code);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. rate_workspace_member_cost_role
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_workspace_member_cost_role (
    id                      UUID            NOT NULL,
    workspace_id            UUID            NOT NULL,
    workspace_member_id     UUID            NOT NULL,
    user_id                 UUID            NOT NULL,
    cost_role_id            UUID            NOT NULL,
    is_default              BOOLEAN         NOT NULL DEFAULT TRUE,
    effective_from          DATE            NOT NULL,
    effective_to            DATE,
    status                  VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_rate_workspace_member_cost_role PRIMARY KEY (id),
    CONSTRAINT ck_rate_workspace_member_cost_role_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT ck_rate_workspace_member_cost_role_dates
        CHECK (effective_to IS NULL OR effective_to >= effective_from),
    CONSTRAINT fk_rate_workspace_member_cost_role_cost_role
        FOREIGN KEY (cost_role_id) REFERENCES rate_cost_role (id)
);

CREATE INDEX IF NOT EXISTS idx_rate_workspace_member_cost_role_workspace
    ON rate_workspace_member_cost_role (workspace_id);
CREATE INDEX IF NOT EXISTS idx_rate_workspace_member_cost_role_member
    ON rate_workspace_member_cost_role (workspace_member_id);
CREATE INDEX IF NOT EXISTS idx_rate_workspace_member_cost_role_role
    ON rate_workspace_member_cost_role (cost_role_id);
CREATE INDEX IF NOT EXISTS idx_rate_workspace_member_cost_role_status
    ON rate_workspace_member_cost_role (status);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. rate_card
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_card (
    id                      UUID            NOT NULL,
    code                    VARCHAR(100)    NOT NULL,
    name                    VARCHAR(255)    NOT NULL,
    description             TEXT,
    scope                   VARCHAR(50)     NOT NULL,
    organization_id         UUID,
    workspace_id            UUID,
    client_id               UUID,
    project_id              UUID,
    default_currency_code   VARCHAR(10)     NOT NULL,
    is_default              BOOLEAN         NOT NULL DEFAULT FALSE,
    status                  VARCHAR(50)     NOT NULL DEFAULT 'DRAFT',
    current_version_id      UUID,
    built_in                BOOLEAN         NOT NULL DEFAULT FALSE,
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_rate_card PRIMARY KEY (id),
    CONSTRAINT ck_rate_card_scope CHECK (scope IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE', 'CLIENT', 'PROJECT')),
    CONSTRAINT ck_rate_card_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'ARCHIVED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_rate_card_scope_code
    ON rate_card (
        scope,
        COALESCE(organization_id, '00000000-0000-0000-0000-000000000000'),
        COALESCE(workspace_id, '00000000-0000-0000-0000-000000000000'),
        COALESCE(client_id, '00000000-0000-0000-0000-000000000000'),
        COALESCE(project_id, '00000000-0000-0000-0000-000000000000'),
        code
    );

CREATE UNIQUE INDEX IF NOT EXISTS uq_rate_card_workspace_default_active
    ON rate_card (workspace_id)
    WHERE is_default = TRUE AND status = 'ACTIVE' AND scope = 'WORKSPACE';

CREATE INDEX IF NOT EXISTS idx_rate_card_scope ON rate_card (scope);
CREATE INDEX IF NOT EXISTS idx_rate_card_org ON rate_card (organization_id);
CREATE INDEX IF NOT EXISTS idx_rate_card_workspace ON rate_card (workspace_id);
CREATE INDEX IF NOT EXISTS idx_rate_card_status ON rate_card (status);
CREATE INDEX IF NOT EXISTS idx_rate_card_code ON rate_card (code);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. rate_card_version
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_card_version (
    id                  UUID            NOT NULL,
    rate_card_id        UUID            NOT NULL,
    version_number      INT             NOT NULL,
    name                VARCHAR(255),
    description         TEXT,
    effective_from      DATE            NOT NULL,
    effective_to        DATE,
    status              VARCHAR(50)     NOT NULL DEFAULT 'DRAFT',
    published_at        TIMESTAMPTZ,
    published_by        UUID,
    archived_at         TIMESTAMPTZ,
    archived_by         UUID,
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_rate_card_version PRIMARY KEY (id),
    CONSTRAINT uq_rate_card_version_card_number UNIQUE (rate_card_id, version_number),
    CONSTRAINT ck_rate_card_version_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT ck_rate_card_version_dates CHECK (effective_to IS NULL OR effective_to >= effective_from),
    CONSTRAINT fk_rate_card_version_rate_card
        FOREIGN KEY (rate_card_id) REFERENCES rate_card (id)
);

CREATE INDEX IF NOT EXISTS idx_rate_card_version_card ON rate_card_version (rate_card_id);
CREATE INDEX IF NOT EXISTS idx_rate_card_version_status ON rate_card_version (status);
CREATE INDEX IF NOT EXISTS idx_rate_card_version_effective ON rate_card_version (effective_from, effective_to);

ALTER TABLE rate_card
    ADD CONSTRAINT fk_rate_card_current_version
        FOREIGN KEY (current_version_id) REFERENCES rate_card_version (id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. rate_card_line
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_card_line (
    id                      UUID            NOT NULL,
    rate_card_version_id    UUID            NOT NULL,
    cost_role_id            UUID            NOT NULL,
    seniority_level         VARCHAR(100),
    location_code           VARCHAR(100),
    currency_code           VARCHAR(10)     NOT NULL,
    cost_rate_per_hour      DECIMAL(18,4)   NOT NULL,
    billing_rate_per_hour   DECIMAL(18,4),
    notes                   TEXT,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_rate_card_line PRIMARY KEY (id),
    CONSTRAINT ck_rate_card_line_cost_rate CHECK (cost_rate_per_hour > 0),
    CONSTRAINT ck_rate_card_line_billing_rate CHECK (billing_rate_per_hour IS NULL OR billing_rate_per_hour > 0),
    CONSTRAINT fk_rate_card_line_version
        FOREIGN KEY (rate_card_version_id) REFERENCES rate_card_version (id),
    CONSTRAINT fk_rate_card_line_cost_role
        FOREIGN KEY (cost_role_id) REFERENCES rate_cost_role (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_rate_card_line_unique_key
    ON rate_card_line (
        rate_card_version_id,
        cost_role_id,
        COALESCE(seniority_level, ''),
        COALESCE(location_code, ''),
        currency_code
    );

CREATE INDEX IF NOT EXISTS idx_rate_card_line_version ON rate_card_line (rate_card_version_id);
CREATE INDEX IF NOT EXISTS idx_rate_card_line_role ON rate_card_line (cost_role_id);
CREATE INDEX IF NOT EXISTS idx_rate_card_line_currency ON rate_card_line (currency_code);

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. rate_inflation_policy
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rate_inflation_policy (
    id                      UUID            NOT NULL,
    code                    VARCHAR(100)    NOT NULL,
    name                    VARCHAR(255)    NOT NULL,
    description             TEXT,
    scope                   VARCHAR(50)     NOT NULL,
    organization_id         UUID,
    workspace_id            UUID,
    inflation_percent       DECIMAL(8,4)    NOT NULL,
    compound_frequency      VARCHAR(50)     NOT NULL,
    effective_from          DATE            NOT NULL,
    effective_to            DATE,
    status                  VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_rate_inflation_policy PRIMARY KEY (id),
    CONSTRAINT ck_rate_inflation_policy_scope CHECK (scope IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE')),
    CONSTRAINT ck_rate_inflation_policy_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT ck_rate_inflation_policy_frequency CHECK (compound_frequency IN ('ANNUAL', 'MONTHLY', 'NONE')),
    CONSTRAINT ck_rate_inflation_policy_percent CHECK (inflation_percent >= 0),
    CONSTRAINT ck_rate_inflation_policy_dates CHECK (effective_to IS NULL OR effective_to >= effective_from)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_rate_inflation_policy_scope_code
    ON rate_inflation_policy (
        scope,
        COALESCE(organization_id, '00000000-0000-0000-0000-000000000000'),
        COALESCE(workspace_id, '00000000-0000-0000-0000-000000000000'),
        code
    );

CREATE INDEX IF NOT EXISTS idx_rate_inflation_policy_scope ON rate_inflation_policy (scope);
CREATE INDEX IF NOT EXISTS idx_rate_inflation_policy_org ON rate_inflation_policy (organization_id);
CREATE INDEX IF NOT EXISTS idx_rate_inflation_policy_workspace ON rate_inflation_policy (workspace_id);
CREATE INDEX IF NOT EXISTS idx_rate_inflation_policy_status ON rate_inflation_policy (status);
CREATE INDEX IF NOT EXISTS idx_rate_inflation_policy_effective
    ON rate_inflation_policy (effective_from, effective_to);
