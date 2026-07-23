CREATE TABLE app_structure_relation (
    id              UUID          NOT NULL,
    application_id  UUID          NOT NULL,
    workspace_id    UUID          NOT NULL,
    from_node_type  VARCHAR(50)   NOT NULL,
    from_node_id    UUID          NOT NULL,
    to_node_type    VARCHAR(50)   NOT NULL,
    to_node_id      UUID          NOT NULL,
    relation_type   VARCHAR(50)   NOT NULL DEFAULT 'RELATED',
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255)  NOT NULL DEFAULT 'SYSTEM',
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255)  NOT NULL DEFAULT 'SYSTEM',
    version         INTEGER       NOT NULL DEFAULT 0,
    CONSTRAINT pk_app_structure_relation PRIMARY KEY (id),
    CONSTRAINT uq_app_structure_relation UNIQUE (application_id, from_node_type, from_node_id, to_node_type, to_node_id),
    CONSTRAINT ck_app_structure_relation_from_type CHECK (from_node_type IN ('APP_MODULE','SCREEN','API_ENDPOINT','APP_COMPONENT','DATA_ENTITY')),
    CONSTRAINT ck_app_structure_relation_to_type CHECK (to_node_type IN ('APP_MODULE','SCREEN','API_ENDPOINT','APP_COMPONENT','DATA_ENTITY')),
    CONSTRAINT ck_app_structure_relation_type CHECK (relation_type IN ('RELATED','USES','IMPLEMENTS'))
);
CREATE INDEX idx_app_structure_relation_app ON app_structure_relation(application_id);
CREATE INDEX idx_app_structure_relation_from ON app_structure_relation(application_id, from_node_type, from_node_id);
CREATE INDEX idx_app_structure_relation_to ON app_structure_relation(application_id, to_node_type, to_node_id);
