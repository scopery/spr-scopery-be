-- ============================================================
-- V33: Organization Team (org_team, org_team_member, org_team_workspace_assignment)
-- Teams are now owned by Organization, not Workspace.
-- Workspace assignment is a separate join record.
-- ============================================================

CREATE TABLE org_team
(
    id              UUID         NOT NULL,
    organization_id UUID         NOT NULL,
    code            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    status          VARCHAR(50)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT pk_org_team
        PRIMARY KEY (id),

    CONSTRAINT fk_org_team_organization
        FOREIGN KEY (organization_id) REFERENCES workspace_organization (id),

    CONSTRAINT uq_org_team_organization_code
        UNIQUE (organization_id, code)
);

CREATE INDEX idx_org_team_organization_id ON org_team (organization_id);
CREATE INDEX idx_org_team_code            ON org_team (code);
CREATE INDEX idx_org_team_status          ON org_team (status);

-- ============================================================

CREATE TABLE org_team_member
(
    team_id    UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    joined_at  TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),

    CONSTRAINT pk_org_team_member
        PRIMARY KEY (team_id, user_id),

    CONSTRAINT fk_org_team_member_team
        FOREIGN KEY (team_id) REFERENCES org_team (id)
);

CREATE INDEX idx_org_team_member_team_id ON org_team_member (team_id);
CREATE INDEX idx_org_team_member_user_id ON org_team_member (user_id);

-- ============================================================

CREATE TABLE org_team_workspace_assignment
(
    id           UUID        NOT NULL,
    team_id      UUID        NOT NULL,
    workspace_id UUID        NOT NULL,
    assigned_by  UUID,
    assigned_at  TIMESTAMP   NOT NULL,
    status       VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_org_team_workspace_assignment
        PRIMARY KEY (id),

    CONSTRAINT fk_org_team_ws_assignment_team
        FOREIGN KEY (team_id) REFERENCES org_team (id),

    CONSTRAINT fk_org_team_ws_assignment_workspace
        FOREIGN KEY (workspace_id) REFERENCES workspace_workspace (id),

    CONSTRAINT uq_org_team_workspace_assignment
        UNIQUE (team_id, workspace_id)
);

CREATE INDEX idx_org_team_ws_assignment_team_id      ON org_team_workspace_assignment (team_id);
CREATE INDEX idx_org_team_ws_assignment_workspace_id ON org_team_workspace_assignment (workspace_id);
CREATE INDEX idx_org_team_ws_assignment_status       ON org_team_workspace_assignment (status);
