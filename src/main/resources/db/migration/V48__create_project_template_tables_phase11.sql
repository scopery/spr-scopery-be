-- Phase 11: Project Template / Phase Catalog foundation
-- - Harden project_phase_definition (organization_id, ORGANIZATION scope uniqueness)
-- - Create template tables (template, version, phase, wbs, task, dependency)
-- - Add source template traceability on project_project
-- Note: is_system_default is kept as built_in semantics (domain field isSystemDefault / builtIn alias in docs)

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Harden project_phase_definition
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE project_phase_definition
    ADD COLUMN IF NOT EXISTS organization_id UUID;

CREATE INDEX IF NOT EXISTS idx_project_phase_definition_org
    ON project_phase_definition (organization_id);

-- Unique code within ORGANIZATION scope
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_phase_def_organization_code
    ON project_phase_definition (organization_id, code)
    WHERE scope = 'ORGANIZATION';

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. project_template
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template (
    id                  UUID            NOT NULL,
    code                VARCHAR(100)    NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    description         TEXT,
    scope               VARCHAR(50)     NOT NULL,
    organization_id     UUID,
    workspace_id        UUID,
    category            VARCHAR(100),
    visibility          VARCHAR(50),
    status              VARCHAR(50)     NOT NULL DEFAULT 'DRAFT',
    current_version_id  UUID,
    built_in            BOOLEAN         NOT NULL DEFAULT FALSE,
    archived_at         TIMESTAMPTZ,
    archived_by         UUID,
    version             INT             NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_template PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_project_template_system_code
    ON project_template (code)
    WHERE scope = 'SYSTEM';

CREATE UNIQUE INDEX IF NOT EXISTS uq_project_template_organization_code
    ON project_template (organization_id, code)
    WHERE scope = 'ORGANIZATION';

CREATE UNIQUE INDEX IF NOT EXISTS uq_project_template_workspace_code
    ON project_template (workspace_id, code)
    WHERE scope = 'WORKSPACE';

CREATE INDEX IF NOT EXISTS idx_project_template_scope ON project_template (scope);
CREATE INDEX IF NOT EXISTS idx_project_template_status ON project_template (status);
CREATE INDEX IF NOT EXISTS idx_project_template_ws ON project_template (workspace_id);
CREATE INDEX IF NOT EXISTS idx_project_template_org ON project_template (organization_id);
CREATE INDEX IF NOT EXISTS idx_project_template_category ON project_template (category);

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. project_template_version
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template_version (
    id                      UUID            NOT NULL,
    project_template_id     UUID            NOT NULL,
    version_number          INT             NOT NULL,
    name                    VARCHAR(255),
    description             TEXT,
    status                  VARCHAR(50)     NOT NULL DEFAULT 'DRAFT',
    published_at            TIMESTAMPTZ,
    published_by            UUID,
    archived_at             TIMESTAMPTZ,
    archived_by             UUID,
    version                 INT             NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_project_template_version PRIMARY KEY (id),
    CONSTRAINT fk_project_template_version_template
        FOREIGN KEY (project_template_id) REFERENCES project_template (id),
    CONSTRAINT uq_project_template_version_number
        UNIQUE (project_template_id, version_number)
);

CREATE INDEX IF NOT EXISTS idx_project_template_version_template
    ON project_template_version (project_template_id);
CREATE INDEX IF NOT EXISTS idx_project_template_version_status
    ON project_template_version (status);

-- Soft FK for current_version_id (nullable; set after version exists)
ALTER TABLE project_template
    DROP CONSTRAINT IF EXISTS fk_project_template_current_version;
ALTER TABLE project_template
    ADD CONSTRAINT fk_project_template_current_version
        FOREIGN KEY (current_version_id) REFERENCES project_template_version (id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. project_template_phase
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template_phase (
    id                              UUID            NOT NULL,
    template_version_id             UUID            NOT NULL,
    phase_definition_id             UUID,
    code                            VARCHAR(100),
    name                            VARCHAR(255)    NOT NULL,
    description                     TEXT,
    display_order                   INT             NOT NULL,
    default_duration_days           INT,
    start_offset_days               INT,
    deliverable_document_type_id    UUID,
    version                         INT             NOT NULL DEFAULT 0,
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255),
    CONSTRAINT pk_project_template_phase PRIMARY KEY (id),
    CONSTRAINT fk_project_template_phase_version
        FOREIGN KEY (template_version_id) REFERENCES project_template_version (id),
    CONSTRAINT fk_project_template_phase_definition
        FOREIGN KEY (phase_definition_id) REFERENCES project_phase_definition (id),
    CONSTRAINT uq_project_template_phase_display_order
        UNIQUE (template_version_id, display_order)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_project_template_phase_code
    ON project_template_phase (template_version_id, code)
    WHERE code IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_project_template_phase_version
    ON project_template_phase (template_version_id);
CREATE INDEX IF NOT EXISTS idx_project_template_phase_def
    ON project_template_phase (phase_definition_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. project_template_wbs_node
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template_wbs_node (
    id                              UUID            NOT NULL,
    template_version_id             UUID            NOT NULL,
    parent_id                       UUID,
    template_phase_id               UUID,
    code                            VARCHAR(100),
    title                           VARCHAR(255)    NOT NULL,
    description                     TEXT,
    node_type                       VARCHAR(50),
    depth                           INT             NOT NULL DEFAULT 0,
    order_index                     INT             NOT NULL DEFAULT 0,
    deliverable_document_type_id    UUID,
    version                         INT             NOT NULL DEFAULT 0,
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255),
    CONSTRAINT pk_project_template_wbs_node PRIMARY KEY (id),
    CONSTRAINT fk_project_template_wbs_version
        FOREIGN KEY (template_version_id) REFERENCES project_template_version (id),
    CONSTRAINT fk_project_template_wbs_parent
        FOREIGN KEY (parent_id) REFERENCES project_template_wbs_node (id),
    CONSTRAINT fk_project_template_wbs_phase
        FOREIGN KEY (template_phase_id) REFERENCES project_template_phase (id),
    CONSTRAINT chk_project_template_wbs_depth CHECK (depth >= 0)
);

CREATE INDEX IF NOT EXISTS idx_project_template_wbs_version
    ON project_template_wbs_node (template_version_id);
CREATE INDEX IF NOT EXISTS idx_project_template_wbs_parent
    ON project_template_wbs_node (parent_id);
CREATE INDEX IF NOT EXISTS idx_project_template_wbs_phase
    ON project_template_wbs_node (template_phase_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. project_template_task
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template_task (
    id                              UUID            NOT NULL,
    template_version_id             UUID            NOT NULL,
    template_phase_id               UUID            NOT NULL,
    template_wbs_node_id            UUID,
    code                            VARCHAR(100),
    title                           VARCHAR(255)    NOT NULL,
    description                     TEXT,
    default_priority                VARCHAR(50),
    estimate_hours                  NUMERIC(12, 2),
    due_offset_days                 INT,
    start_offset_days               INT,
    default_assignee_role_code      VARCHAR(100),
    deliverable_document_type_id    UUID,
    version                         INT             NOT NULL DEFAULT 0,
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255),
    CONSTRAINT pk_project_template_task PRIMARY KEY (id),
    CONSTRAINT fk_project_template_task_version
        FOREIGN KEY (template_version_id) REFERENCES project_template_version (id),
    CONSTRAINT fk_project_template_task_phase
        FOREIGN KEY (template_phase_id) REFERENCES project_template_phase (id),
    CONSTRAINT fk_project_template_task_wbs
        FOREIGN KEY (template_wbs_node_id) REFERENCES project_template_wbs_node (id),
    CONSTRAINT chk_project_template_task_estimate
        CHECK (estimate_hours IS NULL OR estimate_hours > 0)
);

CREATE INDEX IF NOT EXISTS idx_project_template_task_version
    ON project_template_task (template_version_id);
CREATE INDEX IF NOT EXISTS idx_project_template_task_phase
    ON project_template_task (template_phase_id);
CREATE INDEX IF NOT EXISTS idx_project_template_task_wbs
    ON project_template_task (template_wbs_node_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. project_template_task_dependency
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS project_template_task_dependency (
    id                              UUID            NOT NULL,
    template_version_id             UUID            NOT NULL,
    predecessor_template_task_id    UUID            NOT NULL,
    successor_template_task_id      UUID            NOT NULL,
    dependency_type                 VARCHAR(50)     NOT NULL DEFAULT 'FINISH_TO_START',
    lag_days                        INT             NOT NULL DEFAULT 0,
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by                      VARCHAR(255),
    updated_by                      VARCHAR(255),
    CONSTRAINT pk_project_template_task_dependency PRIMARY KEY (id),
    CONSTRAINT fk_project_template_dep_version
        FOREIGN KEY (template_version_id) REFERENCES project_template_version (id),
    CONSTRAINT fk_project_template_dep_pred
        FOREIGN KEY (predecessor_template_task_id) REFERENCES project_template_task (id),
    CONSTRAINT fk_project_template_dep_succ
        FOREIGN KEY (successor_template_task_id) REFERENCES project_template_task (id),
    CONSTRAINT uq_project_template_dep_edge
        UNIQUE (predecessor_template_task_id, successor_template_task_id, dependency_type),
    CONSTRAINT chk_project_template_dep_not_self
        CHECK (predecessor_template_task_id <> successor_template_task_id)
);

CREATE INDEX IF NOT EXISTS idx_project_template_dep_version
    ON project_template_task_dependency (template_version_id);
CREATE INDEX IF NOT EXISTS idx_project_template_dep_pred
    ON project_template_task_dependency (predecessor_template_task_id);
CREATE INDEX IF NOT EXISTS idx_project_template_dep_succ
    ON project_template_task_dependency (successor_template_task_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- 8. project_project source template traceability
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE project_project
    ADD COLUMN IF NOT EXISTS source_template_id UUID,
    ADD COLUMN IF NOT EXISTS source_template_version_id UUID,
    ADD COLUMN IF NOT EXISTS source_template_applied_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_project_project_source_template
    ON project_project (source_template_id);
CREATE INDEX IF NOT EXISTS idx_project_project_source_template_version
    ON project_project (source_template_version_id);

ALTER TABLE project_project
    DROP CONSTRAINT IF EXISTS fk_project_project_source_template;
ALTER TABLE project_project
    ADD CONSTRAINT fk_project_project_source_template
        FOREIGN KEY (source_template_id) REFERENCES project_template (id);

ALTER TABLE project_project
    DROP CONSTRAINT IF EXISTS fk_project_project_source_template_version;
ALTER TABLE project_project
    ADD CONSTRAINT fk_project_project_source_template_version
        FOREIGN KEY (source_template_version_id) REFERENCES project_template_version (id);
