-- ============================================================
-- V28: IAM Access Grant Permission Action
-- Moves grant authority storage from legacy right_id to permission_action_id.
-- iam_right remains as a legacy bridge during migration.
-- ============================================================

CREATE TABLE iam_access_grant_permission_action
(
    grant_id             UUID      NOT NULL,
    permission_action_id UUID      NOT NULL,
    created_at           TIMESTAMP NOT NULL,
    created_by           VARCHAR(100),

    CONSTRAINT pk_iam_access_grant_permission_action
        PRIMARY KEY (grant_id, permission_action_id),
    CONSTRAINT fk_iam_agpa_access_grant
        FOREIGN KEY (grant_id) REFERENCES iam_access_grant (id),
    CONSTRAINT fk_iam_agpa_permission_action
        FOREIGN KEY (permission_action_id) REFERENCES iam_permission_action (id)
);

CREATE INDEX idx_iam_agpa_grant_id
    ON iam_access_grant_permission_action (grant_id);

CREATE INDEX idx_iam_agpa_permission_action_id
    ON iam_access_grant_permission_action (permission_action_id);

INSERT INTO iam_access_grant_permission_action (
    grant_id,
    permission_action_id,
    created_at,
    created_by
)
SELECT
    agr.grant_id,
    ipa.id,
    COALESCE(agr.created_at, CURRENT_TIMESTAMP),
    agr.created_by
FROM iam_access_grant_right agr
JOIN iam_permission_action ipa ON ipa.right_id = agr.right_id
ON CONFLICT (grant_id, permission_action_id) DO NOTHING;
