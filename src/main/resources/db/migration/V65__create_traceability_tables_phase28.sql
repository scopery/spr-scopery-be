-- Phase 28: Application registry / requirements / traceability
CREATE TABLE IF NOT EXISTS app_registry_application (
    id UUID NOT NULL, workspace_id UUID NOT NULL, code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, status VARCHAR(50) NOT NULL, owner_user_id UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_application PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_application_code UNIQUE (workspace_id, code)
);
CREATE TABLE IF NOT EXISTS app_registry_project_mapping (
    id UUID NOT NULL, application_id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL,
    mapping_type VARCHAR(50) NOT NULL DEFAULT 'PRIMARY', status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_app_registry_project_mapping PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_project_mapping UNIQUE (application_id, project_id)
);
CREATE TABLE IF NOT EXISTS requirements_requirement (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, application_id UUID,
    code VARCHAR(100), title VARCHAR(255) NOT NULL, description TEXT, requirement_type VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, owner_user_id UUID,
    current_version_number INT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255), archived_at TIMESTAMPTZ, archived_by UUID,
    CONSTRAINT pk_requirements_requirement PRIMARY KEY (id),
    CONSTRAINT ck_requirements_requirement_status CHECK (status IN ('DRAFT','READY','APPROVED','IMPLEMENTED','VERIFIED','DEPRECATED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_requirements_requirement_project ON requirements_requirement(project_id);
CREATE TABLE IF NOT EXISTS traceability_link (
    id UUID NOT NULL, project_id UUID NOT NULL, source_type VARCHAR(100) NOT NULL, source_id UUID NOT NULL,
    target_type VARCHAR(100) NOT NULL, target_id UUID NOT NULL, link_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), archived_at TIMESTAMPTZ,
    CONSTRAINT pk_traceability_link PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_traceability_link_project ON traceability_link(project_id);
CREATE TABLE IF NOT EXISTS app_registry_screen (
    id UUID NOT NULL, application_id UUID NOT NULL, project_id UUID, code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    route_path VARCHAR(500), status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_screen PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS app_registry_api_endpoint (
    id UUID NOT NULL, application_id UUID NOT NULL, project_id UUID, method VARCHAR(20) NOT NULL, path_pattern VARCHAR(500) NOT NULL,
    name VARCHAR(255), status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_app_registry_api_endpoint PRIMARY KEY (id)
);
