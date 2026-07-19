-- Phase 29: External party / stakeholder
CREATE TABLE IF NOT EXISTS external_organization (
    id UUID NOT NULL, workspace_id UUID NOT NULL, code VARCHAR(100), name VARCHAR(255) NOT NULL,
    organization_type VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, tax_id VARCHAR(100),
    website VARCHAR(500), notes TEXT,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255), archived_at TIMESTAMPTZ, archived_by UUID,
    CONSTRAINT pk_external_organization PRIMARY KEY (id),
    CONSTRAINT ck_external_organization_type CHECK (organization_type IN ('CLIENT','VENDOR','PARTNER','REGULATOR','OTHER')),
    CONSTRAINT ck_external_organization_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_external_organization_workspace ON external_organization(workspace_id);

CREATE TABLE IF NOT EXISTS external_contact (
    id UUID NOT NULL, workspace_id UUID NOT NULL, organization_id UUID, first_name VARCHAR(150) NOT NULL,
    last_name VARCHAR(150) NOT NULL, email VARCHAR(320), phone VARCHAR(50), title VARCHAR(255),
    status VARCHAR(50) NOT NULL, primary_flag BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255), archived_at TIMESTAMPTZ, archived_by UUID,
    CONSTRAINT pk_external_contact PRIMARY KEY (id),
    CONSTRAINT fk_external_contact_org FOREIGN KEY (organization_id) REFERENCES external_organization(id),
    CONSTRAINT ck_external_contact_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS project_external_party_relationship (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, organization_id UUID NOT NULL,
    relationship_type VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, notes TEXT,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_project_external_party_relationship PRIMARY KEY (id),
    CONSTRAINT uq_project_external_party_relationship UNIQUE (project_id, organization_id, relationship_type)
);

CREATE TABLE IF NOT EXISTS project_stakeholder (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, contact_id UUID, organization_id UUID,
    internal_user_id UUID, stakeholder_role VARCHAR(100) NOT NULL, influence VARCHAR(50), interest VARCHAR(50),
    status VARCHAR(50) NOT NULL, client_facing BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255), archived_at TIMESTAMPTZ,
    CONSTRAINT pk_project_stakeholder PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_project_stakeholder_project ON project_stakeholder(project_id);

CREATE TABLE IF NOT EXISTS project_approval_authority (
    id UUID NOT NULL, project_id UUID NOT NULL, stakeholder_id UUID NOT NULL, authority_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL, notes TEXT,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_project_approval_authority PRIMARY KEY (id)
);
