-- Phase 27: Document Hub / Generation foundation
CREATE TABLE IF NOT EXISTS document_folder (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, parent_folder_id UUID,
    name VARCHAR(255) NOT NULL, description TEXT, status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT, archived_at TIMESTAMPTZ, archived_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_document_folder PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_document_folder_project ON document_folder(project_id);

CREATE TABLE IF NOT EXISTS document_document (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, folder_id UUID,
    document_type_code VARCHAR(100), code VARCHAR(100), title VARCHAR(255) NOT NULL,
    description TEXT, status VARCHAR(50) NOT NULL, classification VARCHAR(50),
    current_version_id UUID, locked BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at TIMESTAMPTZ, approved_by UUID, archived_at TIMESTAMPTZ, archived_by UUID,
    trace_id VARCHAR(100), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_document_document PRIMARY KEY (id),
    CONSTRAINT ck_document_document_status CHECK (status IN ('DRAFT','IN_REVIEW','APPROVED','PUBLISHED','ARCHIVED','SUPERSEDED'))
);
CREATE INDEX IF NOT EXISTS idx_document_document_project ON document_document(project_id);

CREATE TABLE IF NOT EXISTS document_version (
    id UUID NOT NULL, document_id UUID NOT NULL, project_id UUID, workspace_id UUID NOT NULL,
    version_number INT NOT NULL, storage_key VARCHAR(500) NOT NULL, file_name VARCHAR(500) NOT NULL,
    content_type VARCHAR(200), file_size_bytes BIGINT, checksum VARCHAR(128),
    status VARCHAR(50) NOT NULL, change_notes TEXT, uploaded_by UUID, uploaded_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_document_version PRIMARY KEY (id),
    CONSTRAINT fk_document_version_document FOREIGN KEY (document_id) REFERENCES document_document(id),
    CONSTRAINT ck_document_version_status CHECK (status IN ('DRAFT','CURRENT','SUPERSEDED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS document_link (
    id UUID NOT NULL, document_id UUID NOT NULL, project_id UUID, target_type VARCHAR(100) NOT NULL,
    target_id UUID NOT NULL, link_type VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_document_link PRIMARY KEY (id),
    CONSTRAINT fk_document_link_document FOREIGN KEY (document_id) REFERENCES document_document(id)
);

CREATE TABLE IF NOT EXISTS document_share (
    id UUID NOT NULL, document_id UUID NOT NULL, project_id UUID, share_type VARCHAR(50) NOT NULL,
    grantee_type VARCHAR(50) NOT NULL, grantee_id UUID, expires_at TIMESTAMPTZ, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_document_share PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS document_access_log (
    id UUID NOT NULL, document_id UUID NOT NULL, document_version_id UUID, project_id UUID,
    actor_user_id UUID, action VARCHAR(50) NOT NULL, ip_address VARCHAR(100), occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_document_access_log PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS document_template (
    id UUID NOT NULL, workspace_id UUID NOT NULL, code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, category VARCHAR(100), status VARCHAR(50) NOT NULL,
    current_version_id UUID, archived_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_document_template PRIMARY KEY (id),
    CONSTRAINT uq_document_template_code UNIQUE (workspace_id, code)
);

CREATE TABLE IF NOT EXISTS document_template_version (
    id UUID NOT NULL, template_id UUID NOT NULL, version_number INT NOT NULL, body_template TEXT NOT NULL,
    output_format VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_document_template_version PRIMARY KEY (id),
    CONSTRAINT fk_document_template_version_template FOREIGN KEY (template_id) REFERENCES document_template(id)
);

CREATE TABLE IF NOT EXISTS generated_document_job (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, template_id UUID, template_version_id UUID,
    job_type VARCHAR(100) NOT NULL, status VARCHAR(50) NOT NULL, source_type VARCHAR(100), source_id UUID,
    output_document_id UUID, error_message TEXT, requested_by UUID,
    started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_generated_document_job PRIMARY KEY (id),
    CONSTRAINT ck_generated_document_job_status CHECK (status IN ('QUEUED','RUNNING','SUCCEEDED','FAILED','CANCELLED'))
);
