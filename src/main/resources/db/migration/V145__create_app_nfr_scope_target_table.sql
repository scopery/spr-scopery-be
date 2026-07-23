CREATE TABLE IF NOT EXISTS app_nfr_scope_target (
    nfr_id      UUID        NOT NULL,
    target_id   UUID        NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    CONSTRAINT pk_app_nfr_scope_target PRIMARY KEY (nfr_id, target_id),
    CONSTRAINT fk_app_nfr_scope_target_nfr FOREIGN KEY (nfr_id)
        REFERENCES app_non_functional_item(id) ON DELETE CASCADE,
    CONSTRAINT ck_app_nfr_scope_target_type
        CHECK (target_type IN ('MODULE', 'FUNCTION', 'SCREEN'))
);

CREATE INDEX IF NOT EXISTS idx_app_nfr_scope_target_nfr_id   ON app_nfr_scope_target(nfr_id);
CREATE INDEX IF NOT EXISTS idx_app_nfr_scope_target_target_id ON app_nfr_scope_target(target_id);
