-- Phase 41: upgrade the real Document Hub version table to S3-compatible object storage.
-- Repository-aligned table/columns: documenthub_version, storage_key, content_type, checksum.
-- This migration intentionally does not create or reference document_version, mime_type, or checksum_sha256.

ALTER TABLE documenthub_version
    ADD COLUMN storage_provider VARCHAR(32) NULL,
    ADD COLUMN storage_status VARCHAR(32) NULL,
    ADD COLUMN storage_etag VARCHAR(255) NULL,
    ADD COLUMN upload_completed_at TIMESTAMPTZ NULL,
    ADD COLUMN storage_verified_at TIMESTAMPTZ NULL;

UPDATE documenthub_version
SET storage_provider = 'LEGACY_REFERENCE',
    storage_status = 'LEGACY_REFERENCE'
WHERE storage_key IS NOT NULL
  AND storage_status IS NULL;

UPDATE documenthub_version
SET storage_status = 'NOT_APPLICABLE'
WHERE storage_key IS NULL
  AND storage_status IS NULL;

ALTER TABLE documenthub_version
    ALTER COLUMN storage_status SET DEFAULT 'NOT_APPLICABLE',
    ALTER COLUMN storage_status SET NOT NULL;

ALTER TABLE documenthub_version
    ADD CONSTRAINT ck_documenthub_version_storage_provider_phase41
    CHECK (storage_provider IS NULL OR storage_provider IN ('MINIO','CLOUDFLARE_R2','LEGACY_REFERENCE')) NOT VALID;

ALTER TABLE documenthub_version
    ADD CONSTRAINT ck_documenthub_version_storage_status_phase41
    CHECK (storage_status IN ('NOT_APPLICABLE','PENDING_UPLOAD','AVAILABLE','FAILED','DELETED','LEGACY_REFERENCE')) NOT VALID;

ALTER TABLE documenthub_version
    ADD CONSTRAINT ck_documenthub_version_storage_consistency_phase41
    CHECK (
        (storage_status = 'NOT_APPLICABLE' AND storage_key IS NULL)
        OR (storage_status = 'LEGACY_REFERENCE'
            AND storage_key IS NOT NULL
            AND storage_provider = 'LEGACY_REFERENCE')
        OR (storage_status = 'PENDING_UPLOAD'
            AND storage_key IS NOT NULL
            AND storage_provider IN ('MINIO','CLOUDFLARE_R2'))
        OR (storage_status = 'AVAILABLE'
            AND storage_key IS NOT NULL
            AND storage_provider IN ('MINIO','CLOUDFLARE_R2')
            AND checksum IS NOT NULL
            AND length(trim(checksum)) > 0
            AND storage_verified_at IS NOT NULL)
        OR (storage_status IN ('FAILED','DELETED') AND storage_key IS NOT NULL)
    ) NOT VALID;

CREATE INDEX ix_documenthub_version_storage_status_phase41
    ON documenthub_version(storage_status, storage_provider);

CREATE UNIQUE INDEX uq_documenthub_version_storage_key_phase41
    ON documenthub_version(storage_provider, storage_key)
    WHERE storage_key IS NOT NULL
      AND storage_provider IN ('MINIO','CLOUDFLARE_R2');

-- Validate after application deployment has verified/backfilled legacy references.
-- ALTER TABLE documenthub_version VALIDATE CONSTRAINT ck_documenthub_version_storage_provider_phase41;
-- ALTER TABLE documenthub_version VALIDATE CONSTRAINT ck_documenthub_version_storage_status_phase41;
-- ALTER TABLE documenthub_version VALIDATE CONSTRAINT ck_documenthub_version_storage_consistency_phase41;
