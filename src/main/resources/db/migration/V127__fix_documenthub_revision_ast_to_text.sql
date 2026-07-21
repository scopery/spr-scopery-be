-- Fix: documenthub_revision.ast column type jsonb → text
-- @JdbcTypeCode(SqlTypes.JSON) does not properly bind String → jsonb on INSERT in Hibernate 6.
-- Revisions are immutable snapshots; jsonb type and its CHECK constraint are not needed.

ALTER TABLE documenthub_revision DROP CONSTRAINT IF EXISTS ck_documenthub_revision_ast;
ALTER TABLE documenthub_revision ALTER COLUMN ast TYPE TEXT USING ast::text;
