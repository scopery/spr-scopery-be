-- Phase 12: Resource Calendar / Working Capacity / Project Allocation foundation
-- Tables: capacity_working_calendar, capacity_calendar_day_rule,
--         capacity_calendar_exception, capacity_user_profile,
--         capacity_project_resource_allocation

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. capacity_working_calendar
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS capacity_working_calendar (
    id              UUID            NOT NULL,
    workspace_id    UUID            NOT NULL,
    code            VARCHAR(100)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    timezone        VARCHAR(100)    NOT NULL,
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    status          VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    archived_at     TIMESTAMPTZ,
    archived_by     UUID,
    version         INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_capacity_working_calendar PRIMARY KEY (id),
    CONSTRAINT uq_capacity_working_calendar_workspace_code UNIQUE (workspace_id, code)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_capacity_working_calendar_default_active
    ON capacity_working_calendar (workspace_id)
    WHERE is_default = TRUE AND status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_capacity_working_calendar_workspace
    ON capacity_working_calendar (workspace_id);

CREATE INDEX IF NOT EXISTS idx_capacity_working_calendar_status
    ON capacity_working_calendar (status);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. capacity_calendar_day_rule
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS capacity_calendar_day_rule (
    id                      UUID            NOT NULL,
    working_calendar_id     UUID            NOT NULL,
    day_of_week             VARCHAR(20)     NOT NULL,
    is_working_day          BOOLEAN         NOT NULL,
    start_time              TIME,
    end_time                TIME,
    working_hours           DECIMAL(5,2)    NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_capacity_calendar_day_rule PRIMARY KEY (id),
    CONSTRAINT uq_capacity_calendar_day_rule_calendar_day UNIQUE (working_calendar_id, day_of_week),
    CONSTRAINT ck_capacity_calendar_day_rule_hours CHECK (working_hours >= 0 AND working_hours <= 24),
    CONSTRAINT fk_capacity_calendar_day_rule_calendar
        FOREIGN KEY (working_calendar_id) REFERENCES capacity_working_calendar (id)
);

CREATE INDEX IF NOT EXISTS idx_capacity_calendar_day_rule_calendar
    ON capacity_calendar_day_rule (working_calendar_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. capacity_calendar_exception
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS capacity_calendar_exception (
    id                      UUID            NOT NULL,
    working_calendar_id     UUID            NOT NULL,
    exception_date          DATE            NOT NULL,
    exception_type          VARCHAR(50)     NOT NULL,
    name                    VARCHAR(255)    NOT NULL,
    description             TEXT,
    is_working_day          BOOLEAN         NOT NULL,
    working_hours           DECIMAL(5,2)    NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_capacity_calendar_exception PRIMARY KEY (id),
    CONSTRAINT uq_capacity_calendar_exception_calendar_date UNIQUE (working_calendar_id, exception_date),
    CONSTRAINT ck_capacity_calendar_exception_hours CHECK (working_hours >= 0 AND working_hours <= 24),
    CONSTRAINT fk_capacity_calendar_exception_calendar
        FOREIGN KEY (working_calendar_id) REFERENCES capacity_working_calendar (id)
);

CREATE INDEX IF NOT EXISTS idx_capacity_calendar_exception_calendar
    ON capacity_calendar_exception (working_calendar_id);

CREATE INDEX IF NOT EXISTS idx_capacity_calendar_exception_date
    ON capacity_calendar_exception (exception_date);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. capacity_user_profile
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS capacity_user_profile (
    id                      UUID            NOT NULL,
    workspace_id            UUID            NOT NULL,
    workspace_member_id     UUID            NOT NULL,
    user_id                 UUID            NOT NULL,
    working_calendar_id     UUID            NOT NULL,
    default_daily_hours     DECIMAL(5,2)    NOT NULL,
    focus_factor            DECIMAL(4,3)    NOT NULL,
    capacity_status         VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    effective_from          DATE            NOT NULL,
    effective_to            DATE,
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_capacity_user_profile PRIMARY KEY (id),
    CONSTRAINT ck_capacity_user_profile_daily_hours CHECK (default_daily_hours > 0 AND default_daily_hours <= 24),
    CONSTRAINT ck_capacity_user_profile_focus_factor CHECK (focus_factor > 0 AND focus_factor <= 1),
    CONSTRAINT fk_capacity_user_profile_calendar
        FOREIGN KEY (working_calendar_id) REFERENCES capacity_working_calendar (id)
);

CREATE INDEX IF NOT EXISTS idx_capacity_user_profile_workspace
    ON capacity_user_profile (workspace_id);

CREATE INDEX IF NOT EXISTS idx_capacity_user_profile_member
    ON capacity_user_profile (workspace_member_id);

CREATE INDEX IF NOT EXISTS idx_capacity_user_profile_user
    ON capacity_user_profile (user_id);

CREATE INDEX IF NOT EXISTS idx_capacity_user_profile_status
    ON capacity_user_profile (capacity_status);

CREATE INDEX IF NOT EXISTS idx_capacity_user_profile_calendar
    ON capacity_user_profile (working_calendar_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. capacity_project_resource_allocation
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS capacity_project_resource_allocation (
    id                      UUID            NOT NULL,
    workspace_id            UUID            NOT NULL,
    project_id              UUID            NOT NULL,
    workspace_member_id     UUID            NOT NULL,
    user_id                 UUID            NOT NULL,
    allocation_percent      DECIMAL(5,2)    NOT NULL,
    allocation_type         VARCHAR(50)     NOT NULL DEFAULT 'PLANNED',
    status                  VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    start_date              DATE            NOT NULL,
    end_date                DATE,
    notes                   TEXT,
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_capacity_project_resource_allocation PRIMARY KEY (id),
    CONSTRAINT ck_capacity_project_allocation_percent CHECK (allocation_percent > 0 AND allocation_percent <= 100)
);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_workspace
    ON capacity_project_resource_allocation (workspace_id);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_project
    ON capacity_project_resource_allocation (project_id);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_member
    ON capacity_project_resource_allocation (workspace_member_id);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_user
    ON capacity_project_resource_allocation (user_id);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_status
    ON capacity_project_resource_allocation (status);

CREATE INDEX IF NOT EXISTS idx_capacity_project_allocation_dates
    ON capacity_project_resource_allocation (start_date, end_date);
