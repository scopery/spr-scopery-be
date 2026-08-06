CREATE TABLE spec_pack_version (
    id              UUID    NOT NULL,
    spec_pack_id    UUID    NOT NULL,
    version_number  INTEGER NOT NULL,
    snapshot_json   JSONB   NOT NULL DEFAULT '{}',
    outline_json    JSONB,
    block_count     INTEGER NOT NULL DEFAULT 0,
    asset_count     INTEGER NOT NULL DEFAULT 0,
    change_reason   TEXT,
    created_by      VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_version PRIMARY KEY (id),
    CONSTRAINT uq_spec_pack_version_number UNIQUE (spec_pack_id, version_number),
    CONSTRAINT fk_spec_pack_version_spec_pack FOREIGN KEY (spec_pack_id) REFERENCES spec_pack (id)
);

CREATE INDEX idx_spec_pack_version_pack_id ON spec_pack_version (spec_pack_id);
