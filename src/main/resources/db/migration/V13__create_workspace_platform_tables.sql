-- ============================================================
-- V13: Workspace Platform Foundation
-- Tables: workspace_organization, workspace_workspace,
--         workspace_member, workspace_team, workspace_team_member
-- NOTE: owner_user_id is stored as UUID reference only.
--       No FK to iam_user — IAM user table does not exist yet.
-- ============================================================

CREATE TABLE workspace_organization
(
    id            UUID         NOT NULL,
    code          VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    owner_user_id UUID         NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT pk_workspace_organization
        PRIMARY KEY (id),

    CONSTRAINT uq_workspace_organization_code
        UNIQUE (code)
);

CREATE INDEX idx_workspace_organization_code          ON workspace_organization (code);
CREATE INDEX idx_workspace_organization_owner_user_id ON workspace_organization (owner_user_id);
CREATE INDEX idx_workspace_organization_status        ON workspace_organization (status);

-- ============================================================

CREATE TABLE workspace_workspace
(
    id                 UUID         NOT NULL,
    organization_id    UUID         NOT NULL,
    code               VARCHAR(100) NOT NULL,
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    owner_user_id      UUID         NOT NULL,
    default_visibility VARCHAR(50)  NOT NULL,
    status             VARCHAR(50)  NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL,
    created_by         VARCHAR(100),
    updated_by         VARCHAR(100),

    CONSTRAINT pk_workspace_workspace
        PRIMARY KEY (id),

    CONSTRAINT fk_workspace_workspace_organization
        FOREIGN KEY (organization_id) REFERENCES workspace_organization (id),

    CONSTRAINT uq_workspace_workspace_organization_code
        UNIQUE (organization_id, code)
);

CREATE INDEX idx_workspace_workspace_organization_id  ON workspace_workspace (organization_id);
CREATE INDEX idx_workspace_workspace_code             ON workspace_workspace (code);
CREATE INDEX idx_workspace_workspace_owner_user_id    ON workspace_workspace (owner_user_id);
CREATE INDEX idx_workspace_workspace_status           ON workspace_workspace (status);
CREATE INDEX idx_workspace_workspace_default_visibility ON workspace_workspace (default_visibility);

-- ============================================================
-- No role column — role/permission handled later by IAM.
-- ============================================================

CREATE TABLE workspace_member
(
    id           UUID        NOT NULL,
    workspace_id UUID        NOT NULL,
    user_id      UUID        NOT NULL,
    status       VARCHAR(50) NOT NULL,
    joined_at    TIMESTAMP   NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_workspace_member
        PRIMARY KEY (id),

    CONSTRAINT fk_workspace_member_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspace_workspace (id),

    CONSTRAINT uq_workspace_member_workspace_user
        UNIQUE (workspace_id, user_id)
);

CREATE INDEX idx_workspace_member_workspace_id ON workspace_member (workspace_id);
CREATE INDEX idx_workspace_member_user_id      ON workspace_member (user_id);
CREATE INDEX idx_workspace_member_status       ON workspace_member (status);

-- ============================================================

CREATE TABLE workspace_team
(
    id           UUID         NOT NULL,
    workspace_id UUID         NOT NULL,
    code         VARCHAR(100) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    status       VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_workspace_team
        PRIMARY KEY (id),

    CONSTRAINT fk_workspace_team_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspace_workspace (id),

    CONSTRAINT uq_workspace_team_workspace_code
        UNIQUE (workspace_id, code)
);

CREATE INDEX idx_workspace_team_workspace_id ON workspace_team (workspace_id);
CREATE INDEX idx_workspace_team_code         ON workspace_team (code);
CREATE INDEX idx_workspace_team_status       ON workspace_team (status);

-- ============================================================
-- User must be an active workspace member before being added to a team.
-- Enforced by application-level check (workspace_member must exist + ACTIVE).
-- ============================================================

CREATE TABLE workspace_team_member
(
    team_id    UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    created_by VARCHAR(100),

    CONSTRAINT pk_workspace_team_member
        PRIMARY KEY (team_id, user_id),

    CONSTRAINT fk_workspace_team_member_team
        FOREIGN KEY (team_id) REFERENCES workspace_team (id)
);

CREATE INDEX idx_workspace_team_member_team_id ON workspace_team_member (team_id);
CREATE INDEX idx_workspace_team_member_user_id ON workspace_team_member (user_id);
