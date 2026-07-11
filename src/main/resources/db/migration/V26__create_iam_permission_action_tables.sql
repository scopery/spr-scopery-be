-- ============================================================
-- V26: IAM Permission + Action Catalog
-- permission = grouped capability (for example WORKSPACE_MANAGEMENT)
-- action     = operation under that permission (for example UPDATE)
-- right_id   = compatibility bridge to legacy iam_right grants.
-- ============================================================

CREATE TABLE iam_permission
(
    id                   UUID         NOT NULL,
    code                 VARCHAR(100) NOT NULL,
    module_code          VARCHAR(100) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    description          TEXT,
    resource_scope_level VARCHAR(50)  NOT NULL,
    data_access_policy   VARCHAR(50)  NOT NULL,
    status               VARCHAR(50)  NOT NULL,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),

    CONSTRAINT pk_iam_permission PRIMARY KEY (id),
    CONSTRAINT uq_iam_permission_code UNIQUE (code),
    CONSTRAINT ck_iam_permission_scope_level
        CHECK (resource_scope_level IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE')),
    CONSTRAINT ck_iam_permission_data_access_policy
        CHECK (data_access_policy IN ('OWNER_ONLY', 'ANCESTOR_INHERITED', 'SCOPE_WIDE')),
    CONSTRAINT ck_iam_permission_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_iam_permission_code        ON iam_permission (code);
CREATE INDEX idx_iam_permission_module_code ON iam_permission (module_code);
CREATE INDEX idx_iam_permission_scope_level ON iam_permission (resource_scope_level);
CREATE INDEX idx_iam_permission_status      ON iam_permission (status);

-- ============================================================

CREATE TABLE iam_permission_action
(
    id            UUID         NOT NULL,
    permission_id UUID         NOT NULL,
    action_code   VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    right_id      UUID,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT pk_iam_permission_action PRIMARY KEY (id),
    CONSTRAINT uq_iam_permission_action_permission_action UNIQUE (permission_id, action_code),
    CONSTRAINT fk_iam_permission_action_permission
        FOREIGN KEY (permission_id) REFERENCES iam_permission (id),
    CONSTRAINT fk_iam_permission_action_right
        FOREIGN KEY (right_id) REFERENCES iam_right (id),
    CONSTRAINT ck_iam_permission_action_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_iam_permission_action_permission_id ON iam_permission_action (permission_id);
CREATE INDEX idx_iam_permission_action_action_code   ON iam_permission_action (action_code);
CREATE INDEX idx_iam_permission_action_right_id      ON iam_permission_action (right_id);
CREATE INDEX idx_iam_permission_action_status        ON iam_permission_action (status);

-- ============================================================
-- Action dependencies make implicit requirements explicit.
-- Example: UPDATE requires VIEW.
-- ============================================================

CREATE TABLE iam_permission_action_dependency
(
    action_id          UUID      NOT NULL,
    required_action_id UUID      NOT NULL,
    created_at         TIMESTAMP NOT NULL,
    created_by         VARCHAR(100),

    CONSTRAINT pk_iam_permission_action_dependency PRIMARY KEY (action_id, required_action_id),
    CONSTRAINT fk_iam_permission_action_dependency_action
        FOREIGN KEY (action_id) REFERENCES iam_permission_action (id),
    CONSTRAINT fk_iam_permission_action_dependency_required
        FOREIGN KEY (required_action_id) REFERENCES iam_permission_action (id),
    CONSTRAINT ck_iam_permission_action_dependency_no_self
        CHECK (action_id <> required_action_id)
);

CREATE INDEX idx_iam_permission_action_dependency_action
    ON iam_permission_action_dependency (action_id);
CREATE INDEX idx_iam_permission_action_dependency_required
    ON iam_permission_action_dependency (required_action_id);
