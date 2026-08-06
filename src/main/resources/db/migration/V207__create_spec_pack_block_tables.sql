CREATE TABLE spec_pack_block (
    id                       UUID        NOT NULL,
    spec_pack_id             UUID        NOT NULL,
    block_key                VARCHAR(500) NOT NULL,
    parent_block_id          UUID,
    block_type               VARCHAR(50) NOT NULL,
    title                    TEXT,
    content_format           VARCHAR(50) NOT NULL,
    content_json             JSONB       NOT NULL DEFAULT '{}',
    source_refs_json         JSONB,
    display_order            INTEGER     NOT NULL DEFAULT 0,
    status                   VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    current_revision_number  INTEGER     NOT NULL DEFAULT 1,
    created_by               VARCHAR(255),
    created_at               TIMESTAMPTZ NOT NULL,
    updated_by               VARCHAR(255),
    updated_at               TIMESTAMPTZ NOT NULL,
    deleted_at               TIMESTAMPTZ,

    CONSTRAINT pk_spec_pack_block PRIMARY KEY (id),
    CONSTRAINT fk_spec_pack_block_spec_pack FOREIGN KEY (spec_pack_id) REFERENCES spec_pack (id)
);

CREATE UNIQUE INDEX uq_spec_pack_block_block_key
    ON spec_pack_block (spec_pack_id, block_key)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_spec_pack_block_pack_id       ON spec_pack_block (spec_pack_id);
CREATE INDEX idx_spec_pack_block_parent_id     ON spec_pack_block (parent_block_id);
CREATE INDEX idx_spec_pack_block_block_type    ON spec_pack_block (block_type);
CREATE INDEX idx_spec_pack_block_display_order ON spec_pack_block (spec_pack_id, display_order);

-- ---

CREATE TABLE spec_pack_block_revision (
    id                UUID        NOT NULL,
    spec_pack_block_id UUID       NOT NULL,
    revision_number   INTEGER     NOT NULL,
    title             TEXT,
    content_format    VARCHAR(50) NOT NULL,
    content_json      JSONB       NOT NULL DEFAULT '{}',
    source_refs_json  JSONB,
    change_source     VARCHAR(50) NOT NULL,
    change_comment    TEXT,
    created_by        VARCHAR(255),
    created_at        TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_block_revision PRIMARY KEY (id),
    CONSTRAINT uq_spec_pack_block_revision_number UNIQUE (spec_pack_block_id, revision_number),
    CONSTRAINT fk_spec_pack_block_revision_block FOREIGN KEY (spec_pack_block_id) REFERENCES spec_pack_block (id)
);

CREATE INDEX idx_spec_pack_block_revision_block_id ON spec_pack_block_revision (spec_pack_block_id);
