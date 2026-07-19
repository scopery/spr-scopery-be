-- Phase 27 fix: rename document_* / generated_document_job tables to documenthub_* module prefix (CLAUDE.md §12)

ALTER TABLE IF EXISTS document_folder RENAME TO documenthub_folder;
ALTER TABLE IF EXISTS document_document RENAME TO documenthub_document;
ALTER TABLE IF EXISTS document_version RENAME TO documenthub_version;
ALTER TABLE IF EXISTS document_link RENAME TO documenthub_link;
ALTER TABLE IF EXISTS document_share RENAME TO documenthub_share;
ALTER TABLE IF EXISTS document_access_log RENAME TO documenthub_access_log;
ALTER TABLE IF EXISTS document_template RENAME TO documenthub_template;
ALTER TABLE IF EXISTS document_template_version RENAME TO documenthub_template_version;
ALTER TABLE IF EXISTS generated_document_job RENAME TO documenthub_generated_job;

-- Primary keys
ALTER TABLE documenthub_folder RENAME CONSTRAINT pk_document_folder TO pk_documenthub_folder;
ALTER TABLE documenthub_document RENAME CONSTRAINT pk_document_document TO pk_documenthub_document;
ALTER TABLE documenthub_version RENAME CONSTRAINT pk_document_version TO pk_documenthub_version;
ALTER TABLE documenthub_link RENAME CONSTRAINT pk_document_link TO pk_documenthub_link;
ALTER TABLE documenthub_share RENAME CONSTRAINT pk_document_share TO pk_documenthub_share;
ALTER TABLE documenthub_access_log RENAME CONSTRAINT pk_document_access_log TO pk_documenthub_access_log;
ALTER TABLE documenthub_template RENAME CONSTRAINT pk_document_template TO pk_documenthub_template;
ALTER TABLE documenthub_template_version RENAME CONSTRAINT pk_document_template_version TO pk_documenthub_template_version;
ALTER TABLE documenthub_generated_job RENAME CONSTRAINT pk_generated_document_job TO pk_documenthub_generated_job;

-- Foreign keys
ALTER TABLE documenthub_version RENAME CONSTRAINT fk_document_version_document TO fk_documenthub_version_documenthub_document;
ALTER TABLE documenthub_link RENAME CONSTRAINT fk_document_link_document TO fk_documenthub_link_documenthub_document;
ALTER TABLE documenthub_template_version RENAME CONSTRAINT fk_document_template_version_template TO fk_documenthub_template_version_documenthub_template;

-- Check / unique constraints
ALTER TABLE documenthub_document RENAME CONSTRAINT ck_document_document_status TO ck_documenthub_document_status;
ALTER TABLE documenthub_version RENAME CONSTRAINT ck_document_version_status TO ck_documenthub_version_status;
ALTER TABLE documenthub_template RENAME CONSTRAINT uq_document_template_code TO uq_documenthub_template_code;
ALTER TABLE documenthub_generated_job RENAME CONSTRAINT ck_generated_document_job_status TO ck_documenthub_generated_job_status;

-- Indexes
ALTER INDEX IF EXISTS idx_document_folder_project RENAME TO idx_documenthub_folder_project;
ALTER INDEX IF EXISTS idx_document_document_project RENAME TO idx_documenthub_document_project;
