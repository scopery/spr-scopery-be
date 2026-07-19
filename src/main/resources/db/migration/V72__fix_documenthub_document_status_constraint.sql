-- Align documenthub_document status check constraint with TO-BE spec (Phase 27)
-- Replaces PUBLISHED/SUPERSEDED with ACTIVE/REJECTED/DELETED_SOFT per spec section 6.2
ALTER TABLE documenthub_document DROP CONSTRAINT ck_documenthub_document_status;
ALTER TABLE documenthub_document ADD CONSTRAINT ck_documenthub_document_status
    CHECK (status IN ('DRAFT','ACTIVE','IN_REVIEW','APPROVED','REJECTED','ARCHIVED','DELETED_SOFT'));
