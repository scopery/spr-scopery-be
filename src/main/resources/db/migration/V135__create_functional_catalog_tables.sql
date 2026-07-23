-- Phase: Functional Catalog — Functional Items, NFR, Business Rules, Custom Properties, Anchors
-- Previous migration: V134

CREATE TABLE app_functional_item (
    id                  UUID        NOT NULL,
    project_id          UUID        NOT NULL,
    workspace_id        UUID        NOT NULL,
    code                VARCHAR(50) NOT NULL,
    title               VARCHAR(500) NOT NULL,
    description         TEXT,
    priority            VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status              VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    type                VARCHAR(30) NOT NULL DEFAULT 'FUNCTIONAL',
    acceptance_criteria JSONB,
    version             INTEGER     NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    created_by          VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by          VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_functional_item PRIMARY KEY (id),
    CONSTRAINT uq_app_functional_item_code UNIQUE (project_id, code),
    CONSTRAINT ck_app_functional_item_priority CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    CONSTRAINT ck_app_functional_item_status CHECK (status IN ('DRAFT','IN_REVIEW','APPROVED','IMPLEMENTED','ARCHIVED')),
    CONSTRAINT ck_app_functional_item_type CHECK (type IN ('FUNCTIONAL','USER_STORY','USE_CASE'))
);

CREATE INDEX idx_app_functional_item_project ON app_functional_item(project_id);
CREATE INDEX idx_app_functional_item_status ON app_functional_item(project_id, status);

CREATE TABLE app_non_functional_item (
    id           UUID        NOT NULL,
    project_id   UUID        NOT NULL,
    workspace_id UUID        NOT NULL,
    code         VARCHAR(50) NOT NULL,
    title        VARCHAR(500) NOT NULL,
    description  TEXT,
    category     VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    priority     VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status       VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    target_metric TEXT,
    scope_type   VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',
    scope_ref_id UUID,
    version      INTEGER     NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL,
    created_by   VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by   VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_non_functional_item PRIMARY KEY (id),
    CONSTRAINT uq_app_non_functional_item_code UNIQUE (project_id, code),
    CONSTRAINT ck_app_nfi_category CHECK (category IN ('PERFORMANCE','SECURITY','USABILITY','RELIABILITY','MAINTAINABILITY','SCALABILITY','COMPATIBILITY','OTHER')),
    CONSTRAINT ck_app_nfi_priority CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    CONSTRAINT ck_app_nfi_status CHECK (status IN ('DRAFT','ACTIVE','DEPRECATED','ARCHIVED')),
    CONSTRAINT ck_app_nfi_scope_type CHECK (scope_type IN ('SYSTEM','MODULE','FEATURE'))
);

CREATE INDEX idx_app_non_functional_item_project ON app_non_functional_item(project_id);
CREATE INDEX idx_app_non_functional_item_status ON app_non_functional_item(project_id, status);

CREATE TABLE app_business_rule (
    id                 UUID        NOT NULL,
    functional_item_id UUID        NOT NULL,
    project_id         UUID        NOT NULL,
    code               VARCHAR(50) NOT NULL,
    title              VARCHAR(500) NOT NULL,
    description        TEXT,
    severity           VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status             VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    version            INTEGER     NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL,
    updated_at         TIMESTAMPTZ NOT NULL,
    created_by         VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by         VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_business_rule PRIMARY KEY (id),
    CONSTRAINT fk_app_business_rule_fi FOREIGN KEY (functional_item_id) REFERENCES app_functional_item(id) ON DELETE CASCADE,
    CONSTRAINT uq_app_business_rule_code UNIQUE (functional_item_id, code),
    CONSTRAINT ck_app_business_rule_severity CHECK (severity IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    CONSTRAINT ck_app_business_rule_status CHECK (status IN ('DRAFT','ACTIVE','DEPRECATED','ARCHIVED'))
);

CREATE INDEX idx_app_business_rule_fi ON app_business_rule(functional_item_id);

CREATE TABLE app_functional_item_custom_property (
    id                 UUID         NOT NULL,
    functional_item_id UUID         NOT NULL,
    prop_key           VARCHAR(100) NOT NULL,
    prop_value         TEXT,
    field_type         VARCHAR(30)  NOT NULL DEFAULT 'TEXT',
    created_at         TIMESTAMPTZ  NOT NULL,
    updated_at         TIMESTAMPTZ  NOT NULL,
    created_by         VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by         VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_fi_custom_property PRIMARY KEY (id),
    CONSTRAINT fk_app_fi_custom_property_fi FOREIGN KEY (functional_item_id) REFERENCES app_functional_item(id) ON DELETE CASCADE,
    CONSTRAINT uq_app_fi_custom_property_key UNIQUE (functional_item_id, prop_key),
    CONSTRAINT ck_app_fi_custom_property_field_type CHECK (field_type IN ('TEXT','NUMBER','DATE','BOOLEAN','URL')),
    CONSTRAINT ck_app_fi_custom_property_key_format CHECK (prop_key ~ '^[a-zA-Z_][a-zA-Z0-9_]*$')
);

CREATE INDEX idx_app_fi_custom_property_fi ON app_functional_item_custom_property(functional_item_id);

CREATE TABLE app_functional_item_anchor (
    id                 UUID        NOT NULL,
    functional_item_id UUID        NOT NULL,
    node_type          VARCHAR(50) NOT NULL,
    node_id            UUID        NOT NULL,
    note               TEXT,
    created_at         TIMESTAMPTZ NOT NULL,
    created_by         VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_fi_anchor PRIMARY KEY (id),
    CONSTRAINT fk_app_fi_anchor_fi FOREIGN KEY (functional_item_id) REFERENCES app_functional_item(id) ON DELETE CASCADE,
    CONSTRAINT uq_app_fi_anchor UNIQUE (functional_item_id, node_type, node_id),
    CONSTRAINT ck_app_fi_anchor_node_type CHECK (node_type IN ('SCREEN','API_ENDPOINT','DATA_ENTITY','APP_COMPONENT','APP_MODULE'))
);

CREATE INDEX idx_app_fi_anchor_fi ON app_functional_item_anchor(functional_item_id);
