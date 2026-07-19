-- Phase 32: Search / Navigation / Productivity
CREATE TABLE IF NOT EXISTS productivity_search_index (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID,
    target_type VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    title VARCHAR(500) NOT NULL, subtitle VARCHAR(500), body_text TEXT, status VARCHAR(50),
    tags_json TEXT, visibility_class VARCHAR(50), restricted BOOLEAN NOT NULL DEFAULT FALSE,
    indexed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), source_hash VARCHAR(128),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_search_index PRIMARY KEY (id),
    CONSTRAINT uq_productivity_search_target UNIQUE (target_type, target_id)
);
CREATE INDEX IF NOT EXISTS idx_productivity_search_workspace ON productivity_search_index(workspace_id);
CREATE INDEX IF NOT EXISTS idx_productivity_search_title ON productivity_search_index(title);

CREATE TABLE IF NOT EXISTS productivity_saved_search (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, owner_user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL, scope VARCHAR(50) NOT NULL, query_text TEXT, filters_json TEXT, sort_json TEXT,
    visibility VARCHAR(50) NOT NULL DEFAULT 'PRIVATE', status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_saved_search PRIMARY KEY (id),
    CONSTRAINT ck_productivity_saved_search_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS productivity_saved_view (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, owner_user_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL, name VARCHAR(255) NOT NULL, view_config_json TEXT,
    filters_json TEXT, sort_json TEXT, columns_json TEXT, display_mode VARCHAR(50),
    visibility VARCHAR(50) NOT NULL DEFAULT 'PRIVATE', default_flag BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_saved_view PRIMARY KEY (id),
    CONSTRAINT ck_productivity_saved_view_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS productivity_favorite_item (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL, target_id UUID NOT NULL, label_override VARCHAR(255),
    archived_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_favorite_item PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_productivity_favorite_active ON productivity_favorite_item(user_id, target_type, target_id) WHERE archived_at IS NULL;

CREATE TABLE IF NOT EXISTS productivity_pinned_item (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, scope VARCHAR(50) NOT NULL,
    owner_user_id UUID, target_type VARCHAR(50) NOT NULL, target_id UUID NOT NULL, sort_order INT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_pinned_item PRIMARY KEY (id),
    CONSTRAINT ck_productivity_pinned_scope CHECK (scope IN ('PERSONAL','PROJECT','WORKSPACE','TEAM'))
);

CREATE TABLE IF NOT EXISTS productivity_recent_item (
    id UUID NOT NULL, workspace_id UUID NOT NULL, principal_type VARCHAR(50) NOT NULL,
    user_id UUID, external_portal_account_id UUID,
    target_type VARCHAR(50) NOT NULL, target_id UUID NOT NULL, title_snapshot VARCHAR(500),
    viewed_at TIMESTAMPTZ NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_recent_item PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_productivity_recent_user ON productivity_recent_item(user_id, viewed_at DESC);

CREATE TABLE IF NOT EXISTS productivity_work_inbox_item (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID NOT NULL,
    source_type VARCHAR(50) NOT NULL, source_id UUID NOT NULL, action_type VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL, priority VARCHAR(50), due_at TIMESTAMPTZ, status VARCHAR(50) NOT NULL,
    read_at TIMESTAMPTZ, dismissed_at TIMESTAMPTZ, snoozed_until TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_work_inbox_item PRIMARY KEY (id),
    CONSTRAINT ck_productivity_inbox_status CHECK (status IN ('ACTIVE','READ','DISMISSED','SNOOZED','DONE','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_productivity_inbox_user ON productivity_work_inbox_item(user_id, status);

CREATE TABLE IF NOT EXISTS productivity_command_definition (
    id UUID NOT NULL, code VARCHAR(100) NOT NULL, title VARCHAR(255) NOT NULL, category VARCHAR(100),
    required_permission VARCHAR(100), dangerous BOOLEAN NOT NULL DEFAULT FALSE,
    confirmation_required BOOLEAN NOT NULL DEFAULT FALSE, enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_command_definition PRIMARY KEY (id),
    CONSTRAINT uq_productivity_command_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS productivity_navigation_menu_definition (
    id UUID NOT NULL, code VARCHAR(100) NOT NULL, parent_code VARCHAR(100), label VARCHAR(255) NOT NULL,
    menu_type VARCHAR(50), route_path VARCHAR(500), required_permission VARCHAR(100),
    context_type VARCHAR(50), sort_order INT NOT NULL DEFAULT 0, enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_navigation_menu PRIMARY KEY (id),
    CONSTRAINT uq_productivity_nav_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS productivity_user_navigation_preference (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID NOT NULL,
    preference_json TEXT, default_landing_route VARCHAR(500),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_user_nav_pref PRIMARY KEY (id),
    CONSTRAINT uq_productivity_user_nav UNIQUE (workspace_id, user_id)
);

CREATE TABLE IF NOT EXISTS productivity_search_audit_log (
    id UUID NOT NULL, workspace_id UUID NOT NULL, user_id UUID, query_text TEXT, scope VARCHAR(50),
    result_count INT, sensitive_result_count INT, trace_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_productivity_search_audit PRIMARY KEY (id)
);
