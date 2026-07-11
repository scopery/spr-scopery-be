-- ============================================================
-- V29: Organization Member
-- Canonical record of a user's membership in an organization.
-- membership_type: OWNER | ADMIN | MEMBER
-- ============================================================

CREATE TABLE org_member
(
    id              UUID         NOT NULL,
    organization_id UUID         NOT NULL,
    user_id         UUID         NOT NULL,
    membership_type VARCHAR(50)  NOT NULL,
    status          VARCHAR(50)  NOT NULL,
    joined_at       TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT pk_org_member
        PRIMARY KEY (id),

    CONSTRAINT fk_org_member_organization
        FOREIGN KEY (organization_id) REFERENCES workspace_organization (id),

    CONSTRAINT uq_org_member_organization_user
        UNIQUE (organization_id, user_id)
);

CREATE INDEX idx_org_member_organization_id ON org_member (organization_id);
CREATE INDEX idx_org_member_user_id         ON org_member (user_id);
CREATE INDEX idx_org_member_status          ON org_member (status);
CREATE INDEX idx_org_member_membership_type ON org_member (membership_type);
