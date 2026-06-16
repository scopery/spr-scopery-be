-- ============================================================
-- V14: IAM Access Control Foundation
-- Tables: iam_user, iam_role, iam_right, iam_auth_resource,
--         iam_access_grant, iam_access_grant_right
-- NOTE: iam_user.id is a self-managed UUID reference.
--       No FK from workspace tables to iam_user yet.
-- ============================================================

CREATE TABLE iam_user
(
    id         UUID         NOT NULL,
    username   VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255),
    status     VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT pk_iam_user PRIMARY KEY (id),
    CONSTRAINT uq_iam_user_username UNIQUE (username),
    CONSTRAINT uq_iam_user_email UNIQUE (email)
);

CREATE INDEX idx_iam_user_username ON iam_user (username);
CREATE INDEX idx_iam_user_email    ON iam_user (email);
CREATE INDEX idx_iam_user_status   ON iam_user (status);

-- ============================================================

CREATE TABLE iam_role
(
    id          UUID         NOT NULL,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT pk_iam_role PRIMARY KEY (id),
    CONSTRAINT uq_iam_role_code UNIQUE (code)
);

CREATE INDEX idx_iam_role_code   ON iam_role (code);
CREATE INDEX idx_iam_role_status ON iam_role (status);

-- ============================================================
-- iam_right: seeded by IamRightCatalogInitializer on startup (idempotent).
-- Not created through the API.
-- ============================================================

CREATE TABLE iam_right
(
    id          UUID         NOT NULL,
    code        VARCHAR(100) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    module      VARCHAR(100) NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT pk_iam_right PRIMARY KEY (id),
    CONSTRAINT uq_iam_right_code UNIQUE (code)
);

CREATE INDEX idx_iam_right_code   ON iam_right (code);
CREATE INDEX idx_iam_right_module ON iam_right (module);
CREATE INDEX idx_iam_right_status ON iam_right (status);

-- ============================================================
-- iam_auth_resource: registry of protected resources that grants can target.
-- ============================================================

CREATE TABLE iam_auth_resource
(
    id            UUID         NOT NULL,
    code          VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT pk_iam_auth_resource PRIMARY KEY (id),
    CONSTRAINT uq_iam_auth_resource_code_type UNIQUE (code, resource_type)
);

CREATE INDEX idx_iam_auth_resource_code   ON iam_auth_resource (code);
CREATE INDEX idx_iam_auth_resource_type   ON iam_auth_resource (resource_type);
CREATE INDEX idx_iam_auth_resource_status ON iam_auth_resource (status);

-- ============================================================
-- iam_access_grant: who (subject) has access to what (resource).
-- role_id is optional — a grant can be purely right-based.
-- ============================================================

CREATE TABLE iam_access_grant
(
    id           UUID        NOT NULL,
    subject_type VARCHAR(100) NOT NULL,
    subject_id   UUID        NOT NULL,
    resource_id  UUID        NOT NULL,
    role_id      UUID,
    status       VARCHAR(50) NOT NULL,
    granted_by   UUID,
    granted_at   TIMESTAMP   NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_iam_access_grant PRIMARY KEY (id),
    CONSTRAINT fk_iam_access_grant_resource
        FOREIGN KEY (resource_id) REFERENCES iam_auth_resource (id),
    CONSTRAINT fk_iam_access_grant_role
        FOREIGN KEY (role_id) REFERENCES iam_role (id)
);

CREATE INDEX idx_iam_access_grant_subject  ON iam_access_grant (subject_id);
CREATE INDEX idx_iam_access_grant_resource ON iam_access_grant (resource_id);
CREATE INDEX idx_iam_access_grant_status   ON iam_access_grant (status);

-- ============================================================
-- iam_access_grant_right: junction — which rights belong to a grant.
-- Composite PK (grant_id, right_id). No updatedAt column.
-- ============================================================

CREATE TABLE iam_access_grant_right
(
    grant_id   UUID        NOT NULL,
    right_id   UUID        NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    created_by VARCHAR(100),

    CONSTRAINT pk_iam_access_grant_right PRIMARY KEY (grant_id, right_id),
    CONSTRAINT fk_iam_agr_grant
        FOREIGN KEY (grant_id) REFERENCES iam_access_grant (id),
    CONSTRAINT fk_iam_agr_right
        FOREIGN KEY (right_id) REFERENCES iam_right (id)
);

CREATE INDEX idx_iam_agr_grant_id ON iam_access_grant_right (grant_id);
CREATE INDEX idx_iam_agr_right_id ON iam_access_grant_right (right_id);
