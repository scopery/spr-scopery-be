-- Phase 11.1: Project Core + Phase + WBS + Task Planning Foundation
-- Tables created in FK-safe order

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. project_phase_definition  (standalone catalog, no FK dependencies)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_phase_definition (
    id              UUID            NOT NULL,
    scope           VARCHAR(20)     NOT NULL,
    workspace_id    UUID,
    code            VARCHAR(100)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    display_order   INT             NOT NULL DEFAULT 0,
    is_system_default BOOLEAN       NOT NULL DEFAULT FALSE,
    status          VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    version         INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_project_phase_definition PRIMARY KEY (id)
);

-- Unique code within SYSTEM scope
CREATE UNIQUE INDEX uq_project_phase_def_system_code
    ON project_phase_definition (code)
    WHERE scope = 'SYSTEM';

-- Unique code within a workspace scope
CREATE UNIQUE INDEX uq_project_phase_def_workspace_code
    ON project_phase_definition (workspace_id, code)
    WHERE scope = 'WORKSPACE';

CREATE INDEX idx_project_phase_definition_scope   ON project_phase_definition (scope);
CREATE INDEX idx_project_phase_definition_status  ON project_phase_definition (status);
CREATE INDEX idx_project_phase_definition_ws      ON project_phase_definition (workspace_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. project_project  (refs workspace_id / organization_id / owner_user_id as UUID only — no FK)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_project (
    id                  UUID            NOT NULL,
    workspace_id        UUID            NOT NULL,
    organization_id     UUID,
    code                VARCHAR(100)    NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    owner_user_id       UUID            NOT NULL,
    default_currency    VARCHAR(10),
    planned_start_date  DATE,
    planned_end_date    DATE,
    status              VARCHAR(50)     NOT NULL DEFAULT 'DRAFT',
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_project PRIMARY KEY (id),
    CONSTRAINT uq_project_project_workspace_code UNIQUE (workspace_id, code)
);

CREATE INDEX idx_project_project_workspace_id ON project_project (workspace_id);
CREATE INDEX idx_project_project_status       ON project_project (status);
CREATE INDEX idx_project_project_owner        ON project_project (owner_user_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. project_project_phase
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_project_phase (
    id                  UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    phase_definition_id UUID,
    code                VARCHAR(100)    NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    display_order       INT             NOT NULL,
    planned_start_date  DATE,
    planned_end_date    DATE,
    status              VARCHAR(50)     NOT NULL DEFAULT 'PLANNED',
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_project_phase PRIMARY KEY (id),
    CONSTRAINT fk_project_phase_project
        FOREIGN KEY (project_id) REFERENCES project_project (id),
    CONSTRAINT fk_project_phase_definition
        FOREIGN KEY (phase_definition_id) REFERENCES project_phase_definition (id),
    CONSTRAINT uq_project_phase_project_code
        UNIQUE (project_id, code),
    CONSTRAINT uq_project_phase_display_order
        UNIQUE (project_id, display_order)
);

CREATE INDEX idx_project_project_phase_project_id ON project_project_phase (project_id);
CREATE INDEX idx_project_project_phase_status      ON project_project_phase (status);
CREATE INDEX idx_project_project_phase_def_id      ON project_project_phase (phase_definition_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. project_wbs_node  (self-referencing parent)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_wbs_node (
    id                  UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    project_phase_id    UUID            NOT NULL,
    parent_id           UUID,
    code                VARCHAR(100)    NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    description         TEXT,
    node_type           VARCHAR(50)     NOT NULL,
    level               INT             NOT NULL,
    path                VARCHAR(2000)   NOT NULL,
    sort_order          INT             NOT NULL,
    status              VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_wbs_node PRIMARY KEY (id),
    CONSTRAINT fk_project_wbs_node_project
        FOREIGN KEY (project_id) REFERENCES project_project (id),
    CONSTRAINT fk_project_wbs_node_phase
        FOREIGN KEY (project_phase_id) REFERENCES project_project_phase (id),
    CONSTRAINT fk_project_wbs_node_parent
        FOREIGN KEY (parent_id) REFERENCES project_wbs_node (id),
    CONSTRAINT uq_project_wbs_node_project_code
        UNIQUE (project_id, code)
);

-- Unique sort_order per parent (only enforced when parent exists)
CREATE UNIQUE INDEX uq_project_wbs_node_parent_sort
    ON project_wbs_node (parent_id, sort_order)
    WHERE parent_id IS NOT NULL;

CREATE INDEX idx_project_wbs_node_project_id  ON project_wbs_node (project_id);
CREATE INDEX idx_project_wbs_node_phase_id    ON project_wbs_node (project_phase_id);
CREATE INDEX idx_project_wbs_node_parent_id   ON project_wbs_node (parent_id);
CREATE INDEX idx_project_wbs_node_path        ON project_wbs_node (path);
CREATE INDEX idx_project_wbs_node_status      ON project_wbs_node (status);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. project_task
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_task (
    id                  UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    project_phase_id    UUID            NOT NULL,
    wbs_node_id         UUID            NOT NULL,
    code                VARCHAR(100)    NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    description         TEXT,
    in_charge_user_id   UUID,
    planned_role_code   VARCHAR(100),
    planned_role_name   VARCHAR(255),
    estimate_hours      NUMERIC(10, 2),
    planned_start_date  DATE,
    due_date            DATE,
    priority            VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',
    status              VARCHAR(50)     NOT NULL DEFAULT 'TODO',
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_task PRIMARY KEY (id),
    CONSTRAINT fk_project_task_project
        FOREIGN KEY (project_id) REFERENCES project_project (id),
    CONSTRAINT fk_project_task_phase
        FOREIGN KEY (project_phase_id) REFERENCES project_project_phase (id),
    CONSTRAINT fk_project_task_wbs_node
        FOREIGN KEY (wbs_node_id) REFERENCES project_wbs_node (id),
    CONSTRAINT uq_project_task_project_code
        UNIQUE (project_id, code)
);

CREATE INDEX idx_project_task_project_id       ON project_task (project_id);
CREATE INDEX idx_project_task_phase_id         ON project_task (project_phase_id);
CREATE INDEX idx_project_task_wbs_node_id      ON project_task (wbs_node_id);
CREATE INDEX idx_project_task_status           ON project_task (status);
CREATE INDEX idx_project_task_in_charge_user   ON project_task (in_charge_user_id);
CREATE INDEX idx_project_task_priority         ON project_task (priority);

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. project_task_dependency  (junction-like; no version field)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE project_task_dependency (
    id                  UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    predecessor_task_id UUID            NOT NULL,
    successor_task_id   UUID            NOT NULL,
    dependency_type     VARCHAR(50)     NOT NULL,
    lag_days            INT             NOT NULL DEFAULT 0,
    status              VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_task_dependency PRIMARY KEY (id),
    CONSTRAINT fk_project_task_dep_project
        FOREIGN KEY (project_id) REFERENCES project_project (id),
    CONSTRAINT fk_project_task_dep_predecessor
        FOREIGN KEY (predecessor_task_id) REFERENCES project_task (id),
    CONSTRAINT fk_project_task_dep_successor
        FOREIGN KEY (successor_task_id) REFERENCES project_task (id),
    CONSTRAINT uq_project_task_dep_unique
        UNIQUE (predecessor_task_id, successor_task_id, dependency_type)
);

CREATE INDEX idx_project_task_dep_project_id  ON project_task_dependency (project_id);
CREATE INDEX idx_project_task_dep_predecessor ON project_task_dependency (predecessor_task_id);
CREATE INDEX idx_project_task_dep_successor   ON project_task_dependency (successor_task_id);
CREATE INDEX idx_project_task_dep_status      ON project_task_dependency (status);
