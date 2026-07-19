-- Phase 03: store only SHA-256 hash of org invitation tokens (parity with workspace invitations).
-- Pending invites with plaintext tokens are cancelled — raw tokens cannot be recovered after rename.

ALTER TABLE org_invitation RENAME COLUMN token TO token_hash;

ALTER TABLE org_invitation
    ADD COLUMN IF NOT EXISTS token_hint VARCHAR(16);

UPDATE org_invitation
SET status = 'CANCELLED',
    responded_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE status = 'PENDING';

ALTER TABLE org_invitation DROP CONSTRAINT IF EXISTS uq_org_invitation_token;

ALTER TABLE org_invitation
    ADD CONSTRAINT uq_org_invitation_token_hash UNIQUE (token_hash);

CREATE INDEX IF NOT EXISTS idx_org_invitation_token_hash ON org_invitation (token_hash);
