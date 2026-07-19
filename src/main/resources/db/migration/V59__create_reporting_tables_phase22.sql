-- Phase 22: Reporting / dashboard / export

CREATE TABLE IF NOT EXISTS report_definition (
    id UUID NOT NULL,
    code VARCHAR(150) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    scope VARCHAR(50) NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    required_permissions_json JSONB NOT NULL DEFAULT '[]',
    supported_formats_json JSONB NOT NULL DEFAULT '["CSV","JSON"]',
    default_filters_json JSONB,
    sensitive_fields_json JSONB,
    status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_report_definition PRIMARY KEY (id),
    CONSTRAINT uq_report_definition_code UNIQUE (code),
    CONSTRAINT ck_report_definition_status CHECK (status IN ('ACTIVE','INACTIVE','DEPRECATED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS report_run (
    id UUID NOT NULL,
    report_definition_id UUID NOT NULL,
    workspace_id UUID,
    project_id UUID,
    actor_user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    filters_json JSONB,
    selected_fields_json JSONB,
    access_summary_json JSONB,
    masking_summary_json JSONB,
    result_summary_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_report_run PRIMARY KEY (id),
    CONSTRAINT fk_report_run_definition FOREIGN KEY (report_definition_id) REFERENCES report_definition(id),
    CONSTRAINT ck_report_run_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED','CANCELLED'))
);
CREATE INDEX IF NOT EXISTS idx_report_run_project ON report_run(project_id);

CREATE TABLE IF NOT EXISTS report_snapshot (
    id UUID NOT NULL,
    report_run_id UUID NOT NULL,
    report_definition_id UUID NOT NULL,
    workspace_id UUID,
    project_id UUID,
    actor_user_id UUID NOT NULL,
    snapshot_type VARCHAR(100) NOT NULL,
    data_json JSONB NOT NULL,
    summary_json JSONB,
    masking_summary_json JSONB,
    generated_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_report_snapshot PRIMARY KEY (id),
    CONSTRAINT fk_report_snapshot_run FOREIGN KEY (report_run_id) REFERENCES report_run(id)
);

CREATE TABLE IF NOT EXISTS report_export_job (
    id UUID NOT NULL,
    report_run_id UUID,
    report_snapshot_id UUID,
    report_definition_id UUID NOT NULL,
    workspace_id UUID,
    project_id UUID,
    actor_user_id UUID NOT NULL,
    format VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    file_mime_type VARCHAR(100),
    file_size_bytes BIGINT,
    storage_key VARCHAR(500),
    content_text TEXT,
    download_expires_at TIMESTAMPTZ,
    filters_json JSONB,
    selected_fields_json JSONB,
    masking_summary_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT pk_report_export_job PRIMARY KEY (id),
    CONSTRAINT ck_report_export_job_format CHECK (format IN ('CSV','XLSX','JSON')),
    CONSTRAINT ck_report_export_job_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED','CANCELLED'))
);

CREATE TABLE IF NOT EXISTS project_kpi_snapshot (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    snapshot_date DATE NOT NULL,
    health_status VARCHAR(50) NOT NULL,
    health_score NUMERIC(8,4),
    kpi_json JSONB NOT NULL,
    source_refs_json JSONB,
    formula_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    trace_id VARCHAR(100),
    CONSTRAINT pk_project_kpi_snapshot PRIMARY KEY (id),
    CONSTRAINT fk_project_kpi_snapshot_project FOREIGN KEY (project_id) REFERENCES project_project(id),
    CONSTRAINT ck_project_kpi_snapshot_health CHECK (health_status IN ('GREEN','YELLOW','RED','UNKNOWN'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_kpi_snapshot_project_date
    ON project_kpi_snapshot(project_id, snapshot_date);
