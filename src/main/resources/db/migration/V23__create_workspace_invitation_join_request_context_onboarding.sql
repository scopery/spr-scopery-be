-- ============================================================
-- V23: Workspace invitation, join request, user context,
--      onboarding state, and join_policy on workspace
-- ============================================================

-- 1. Add join_policy to workspace_workspace
ALTER TABLE workspace_workspace
    ADD COLUMN IF NOT EXISTS join_policy VARCHAR(50) NOT NULL DEFAULT 'INVITE_ONLY';

-- 2. workspace_invitation (invitation code stored as SHA-256 hash, never plaintext)
CREATE TABLE workspace_invitation (
    id                    UUID         NOT NULL,
    workspace_id          UUID         NOT NULL,
    created_by_user_id    UUID         NOT NULL,
    invited_email         VARCHAR(255),
    invitation_code_hash  VARCHAR(255) NOT NULL,
    invitation_code_hint  VARCHAR(20),
    status                VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    max_uses              INTEGER,
    used_count            INTEGER      NOT NULL DEFAULT 0,
    expires_at            TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    CONSTRAINT pk_workspace_invitation            PRIMARY KEY (id),
    CONSTRAINT fk_workspace_invitation_workspace  FOREIGN KEY (workspace_id) REFERENCES workspace_workspace(id),
    CONSTRAINT uq_workspace_invitation_code_hash  UNIQUE (invitation_code_hash)
);

CREATE INDEX idx_workspace_invitation_workspace_id ON workspace_invitation(workspace_id);
CREATE INDEX idx_workspace_invitation_status       ON workspace_invitation(status);

-- 3. workspace_join_request
CREATE TABLE workspace_join_request (
    id                    UUID        NOT NULL,
    workspace_id          UUID        NOT NULL,
    requested_by_user_id  UUID        NOT NULL,
    message               TEXT,
    status                VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reviewed_by_user_id   UUID,
    reviewed_at           TIMESTAMP,
    review_note           TEXT,
    created_at            TIMESTAMP   NOT NULL,
    updated_at            TIMESTAMP   NOT NULL,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    CONSTRAINT pk_workspace_join_request           PRIMARY KEY (id),
    CONSTRAINT fk_workspace_join_request_workspace FOREIGN KEY (workspace_id) REFERENCES workspace_workspace(id)
);

CREATE INDEX idx_workspace_join_request_workspace_id ON workspace_join_request(workspace_id);
CREATE INDEX idx_workspace_join_request_status       ON workspace_join_request(status);
-- One PENDING request per user per workspace
CREATE UNIQUE INDEX uq_workspace_join_request_pending
    ON workspace_join_request(workspace_id, requested_by_user_id)
    WHERE status = 'PENDING';

-- 4. workspace_user_context (current workspace selection per user)
CREATE TABLE workspace_user_context (
    user_id                  UUID      NOT NULL,
    current_workspace_id     UUID,
    last_switched_at         TIMESTAMP,
    onboarding_completed_at  TIMESTAMP,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100),
    CONSTRAINT pk_workspace_user_context PRIMARY KEY (user_id)
);

-- 5. workspace_onboarding_state (per-user onboarding state machine)
CREATE TABLE workspace_onboarding_state (
    id                      UUID        NOT NULL,
    user_id                 UUID        NOT NULL,
    status                  VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    current_step            VARCHAR(100) NOT NULL DEFAULT 'CHOOSE_WORKSPACE_OPTION',
    selected_option         VARCHAR(50),
    target_workspace_id     UUID,
    created_organization_id UUID,
    created_workspace_id    UUID,
    invitation_id           UUID,
    join_request_id         UUID,
    failure_reason          TEXT,
    completed_at            TIMESTAMP,
    cancelled_at            TIMESTAMP,
    last_seen_at            TIMESTAMP   NOT NULL,
    created_at              TIMESTAMP   NOT NULL,
    updated_at              TIMESTAMP   NOT NULL,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    CONSTRAINT pk_workspace_onboarding_state      PRIMARY KEY (id),
    CONSTRAINT uq_workspace_onboarding_state_user UNIQUE (user_id)
);

CREATE INDEX idx_workspace_onboarding_state_status ON workspace_onboarding_state(status);
