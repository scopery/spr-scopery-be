-- ============================================================
-- V30: Organization Invitation
-- Tracks pending invitations to join an organization.
-- status: PENDING | ACCEPTED | DECLINED | EXPIRED | CANCELLED
-- ============================================================

CREATE TABLE org_invitation
(
    id              UUID         NOT NULL,
    organization_id UUID         NOT NULL,
    invitee_email   VARCHAR(255) NOT NULL,
    invitee_user_id UUID,
    membership_type VARCHAR(50)  NOT NULL,
    status          VARCHAR(50)  NOT NULL,
    invited_by      UUID         NOT NULL,
    token           VARCHAR(255) NOT NULL,
    expires_at      TIMESTAMP    NOT NULL,
    responded_at    TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT pk_org_invitation
        PRIMARY KEY (id),

    CONSTRAINT fk_org_invitation_organization
        FOREIGN KEY (organization_id) REFERENCES workspace_organization (id),

    CONSTRAINT uq_org_invitation_token
        UNIQUE (token)
);

CREATE INDEX idx_org_invitation_organization_id  ON org_invitation (organization_id);
CREATE INDEX idx_org_invitation_invitee_email    ON org_invitation (invitee_email);
CREATE INDEX idx_org_invitation_invitee_user_id  ON org_invitation (invitee_user_id);
CREATE INDEX idx_org_invitation_status           ON org_invitation (status);
CREATE INDEX idx_org_invitation_expires_at       ON org_invitation (expires_at);
