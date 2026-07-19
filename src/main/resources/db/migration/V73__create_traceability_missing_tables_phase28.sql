-- Phase 28 supplemental: missing tables for application module, component, screen children, data entity, requirement children

CREATE TABLE IF NOT EXISTS app_registry_module (
    id UUID NOT NULL,
    application_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_module PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_module_code UNIQUE (application_id, code),
    CONSTRAINT fk_app_registry_module_application FOREIGN KEY (application_id) REFERENCES app_registry_application(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_module_application ON app_registry_module(application_id);

CREATE TABLE IF NOT EXISTS app_registry_component (
    id UUID NOT NULL,
    application_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    component_type VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_component PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_component_code UNIQUE (application_id, code),
    CONSTRAINT fk_app_registry_component_application FOREIGN KEY (application_id) REFERENCES app_registry_application(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_component_application ON app_registry_component(application_id);

CREATE TABLE IF NOT EXISTS app_registry_screen_section (
    id UUID NOT NULL,
    screen_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_section PRIMARY KEY (id),
    CONSTRAINT fk_app_registry_screen_section_screen FOREIGN KEY (screen_id) REFERENCES app_registry_screen(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_screen_section_screen ON app_registry_screen_section(screen_id);

CREATE TABLE IF NOT EXISTS app_registry_screen_field (
    id UUID NOT NULL,
    screen_id UUID NOT NULL,
    section_id UUID,
    workspace_id UUID NOT NULL,
    field_key VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL,
    field_type VARCHAR(100) NOT NULL,
    description TEXT,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_field PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_screen_field_key UNIQUE (screen_id, field_key),
    CONSTRAINT fk_app_registry_screen_field_screen FOREIGN KEY (screen_id) REFERENCES app_registry_screen(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_screen_field_screen ON app_registry_screen_field(screen_id);

CREATE TABLE IF NOT EXISTS app_registry_screen_action (
    id UUID NOT NULL,
    screen_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    action_code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    action_type VARCHAR(100),
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_action PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_screen_action_code UNIQUE (screen_id, action_code),
    CONSTRAINT fk_app_registry_screen_action_screen FOREIGN KEY (screen_id) REFERENCES app_registry_screen(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_screen_action_screen ON app_registry_screen_action(screen_id);

CREATE TABLE IF NOT EXISTS app_registry_data_entity (
    id UUID NOT NULL,
    application_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    table_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_app_registry_data_entity PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_data_entity_code UNIQUE (application_id, code),
    CONSTRAINT fk_app_registry_data_entity_application FOREIGN KEY (application_id) REFERENCES app_registry_application(id)
);
CREATE INDEX IF NOT EXISTS idx_app_registry_data_entity_application ON app_registry_data_entity(application_id);

CREATE TABLE IF NOT EXISTS requirements_requirement_version (
    id UUID NOT NULL,
    requirement_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    version_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    change_summary TEXT,
    created_by_user_id UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_requirements_requirement_version PRIMARY KEY (id),
    CONSTRAINT uq_requirements_requirement_version_number UNIQUE (requirement_id, version_number),
    CONSTRAINT fk_requirements_requirement_version_req FOREIGN KEY (requirement_id) REFERENCES requirements_requirement(id)
);
CREATE INDEX IF NOT EXISTS idx_requirements_requirement_version_req ON requirements_requirement_version(requirement_id);

CREATE TABLE IF NOT EXISTS requirements_requirement_source (
    id UUID NOT NULL,
    requirement_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    source_type VARCHAR(100) NOT NULL,
    source_reference VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_requirements_requirement_source PRIMARY KEY (id),
    CONSTRAINT fk_requirements_requirement_source_req FOREIGN KEY (requirement_id) REFERENCES requirements_requirement(id)
);
CREATE INDEX IF NOT EXISTS idx_requirements_requirement_source_req ON requirements_requirement_source(requirement_id);

CREATE TABLE IF NOT EXISTS requirements_acceptance_criteria (
    id UUID NOT NULL,
    requirement_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    description TEXT NOT NULL,
    acceptance_type VARCHAR(100) NOT NULL DEFAULT 'FUNCTIONAL',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    display_order INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_requirements_acceptance_criteria PRIMARY KEY (id),
    CONSTRAINT fk_requirements_acceptance_criteria_req FOREIGN KEY (requirement_id) REFERENCES requirements_requirement(id)
);
CREATE INDEX IF NOT EXISTS idx_requirements_acceptance_criteria_req ON requirements_acceptance_criteria(requirement_id);
