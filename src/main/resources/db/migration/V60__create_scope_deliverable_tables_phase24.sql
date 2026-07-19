-- Phase 24: Scope package / deliverable / acceptance

CREATE TABLE IF NOT EXISTS project_scope_package (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    source_quote_version_id UUID,
    source_baseline_id UUID,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_scope_package PRIMARY KEY (id),
    CONSTRAINT fk_project_scope_package_project FOREIGN KEY (project_id) REFERENCES project_project(id),
    CONSTRAINT uq_project_scope_package_code UNIQUE (project_id, code),
    CONSTRAINT ck_project_scope_package_status CHECK (status IN ('DRAFT','READY','APPROVED','CURRENT','ARCHIVED'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_scope_package_current
    ON project_scope_package(project_id) WHERE current_flag = TRUE;

CREATE TABLE IF NOT EXISTS project_scope_item (
    id UUID NOT NULL,
    scope_package_id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    source_quote_line_id UUID,
    source_change_request_id UUID,
    parent_scope_item_id UUID,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    in_scope BOOLEAN NOT NULL DEFAULT TRUE,
    out_of_scope BOOLEAN NOT NULL DEFAULT FALSE,
    priority VARCHAR(50),
    acceptance_required BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL,
    sort_order INT,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_scope_item PRIMARY KEY (id),
    CONSTRAINT fk_project_scope_item_package FOREIGN KEY (scope_package_id) REFERENCES project_scope_package(id),
    CONSTRAINT ck_project_scope_item_flags CHECK (NOT (in_scope = TRUE AND out_of_scope = TRUE)),
    CONSTRAINT ck_project_scope_item_status CHECK (status IN ('DRAFT','ACTIVE','APPROVED','CHANGED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_project_scope_item_package ON project_scope_item(scope_package_id);

CREATE TABLE IF NOT EXISTS project_scope_item_wbs_mapping (
    id UUID NOT NULL,
    scope_item_id UUID NOT NULL,
    project_id UUID NOT NULL,
    wbs_node_id UUID NOT NULL,
    mapping_type VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_project_scope_item_wbs_mapping PRIMARY KEY (id),
    CONSTRAINT fk_project_scope_item_wbs_mapping_item FOREIGN KEY (scope_item_id) REFERENCES project_scope_item(id),
    CONSTRAINT ck_project_scope_item_wbs_mapping_type CHECK (mapping_type IN ('PRIMARY','SUPPORTING','REFERENCE'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_scope_item_wbs_mapping_active
    ON project_scope_item_wbs_mapping(scope_item_id, wbs_node_id, mapping_type) WHERE archived_at IS NULL;

CREATE TABLE IF NOT EXISTS project_deliverable (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_phase_id UUID,
    scope_item_id UUID,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    acceptance_required BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL,
    accepted_at TIMESTAMPTZ,
    accepted_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    reopened_at TIMESTAMPTZ,
    reopened_by UUID,
    reopen_reason TEXT,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_deliverable PRIMARY KEY (id),
    CONSTRAINT fk_project_deliverable_project FOREIGN KEY (project_id) REFERENCES project_project(id),
    CONSTRAINT ck_project_deliverable_status CHECK (status IN (
        'DRAFT','PLANNED','IN_PROGRESS','READY_FOR_REVIEW','IN_REVIEW','ACCEPTED','REJECTED','REWORK_REQUIRED','CANCELLED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_project_deliverable_project ON project_deliverable(project_id);

CREATE TABLE IF NOT EXISTS project_deliverable_task_mapping (
    id UUID NOT NULL,
    deliverable_id UUID NOT NULL,
    project_id UUID NOT NULL,
    task_id UUID NOT NULL,
    mapping_type VARCHAR(50) NOT NULL DEFAULT 'SUPPORTING',
    archived_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_project_deliverable_task_mapping PRIMARY KEY (id),
    CONSTRAINT fk_project_deliverable_task_mapping_deliverable FOREIGN KEY (deliverable_id) REFERENCES project_deliverable(id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_deliverable_task_mapping_active
    ON project_deliverable_task_mapping(deliverable_id, task_id) WHERE archived_at IS NULL;

CREATE TABLE IF NOT EXISTS project_acceptance_criteria (
    id UUID NOT NULL,
    deliverable_id UUID NOT NULL,
    project_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL,
    waive_reason TEXT,
    waived_by UUID,
    waived_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_acceptance_criteria PRIMARY KEY (id),
    CONSTRAINT fk_project_acceptance_criteria_deliverable FOREIGN KEY (deliverable_id) REFERENCES project_deliverable(id),
    CONSTRAINT ck_project_acceptance_criteria_status CHECK (status IN ('OPEN','SATISFIED','FAILED','WAIVED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS project_acceptance_evidence (
    id UUID NOT NULL,
    deliverable_id UUID NOT NULL,
    acceptance_criteria_id UUID,
    project_id UUID NOT NULL,
    evidence_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_text TEXT,
    link_url VARCHAR(1000),
    reference_id VARCHAR(255),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_project_acceptance_evidence PRIMARY KEY (id),
    CONSTRAINT fk_project_acceptance_evidence_deliverable FOREIGN KEY (deliverable_id) REFERENCES project_deliverable(id)
);

CREATE TABLE IF NOT EXISTS project_deliverable_review (
    id UUID NOT NULL,
    deliverable_id UUID NOT NULL,
    project_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    decision VARCHAR(50),
    reviewer_user_id UUID,
    reason TEXT,
    decided_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_project_deliverable_review PRIMARY KEY (id),
    CONSTRAINT fk_project_deliverable_review_deliverable FOREIGN KEY (deliverable_id) REFERENCES project_deliverable(id),
    CONSTRAINT ck_project_deliverable_review_status CHECK (status IN ('OPEN','APPROVED','REJECTED','REWORK_REQUESTED','CANCELLED'))
);

CREATE TABLE IF NOT EXISTS project_deliverable_acceptance (
    id UUID NOT NULL,
    deliverable_id UUID NOT NULL,
    project_id UUID NOT NULL,
    outcome VARCHAR(50) NOT NULL,
    notes TEXT,
    accepted_by UUID NOT NULL,
    accepted_at TIMESTAMPTZ NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_project_deliverable_acceptance PRIMARY KEY (id),
    CONSTRAINT fk_project_deliverable_acceptance_deliverable FOREIGN KEY (deliverable_id) REFERENCES project_deliverable(id),
    CONSTRAINT ck_project_deliverable_acceptance_outcome CHECK (outcome IN ('ACCEPTED','REJECTED','ACCEPTED_WITH_NOTES','REWORK_REQUIRED'))
);
