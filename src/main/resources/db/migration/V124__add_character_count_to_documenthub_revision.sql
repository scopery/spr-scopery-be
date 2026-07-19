-- Align documenthub_revision with DocumentRevisionJpaEntity (character_count).
-- V102 created character_count on documenthub_content only.

ALTER TABLE documenthub_revision
    ADD COLUMN IF NOT EXISTS character_count INTEGER NULL;

UPDATE documenthub_revision
SET character_count = COALESCE(character_count, COALESCE(length(plain_text), 0))
WHERE character_count IS NULL;

ALTER TABLE documenthub_revision
    ALTER COLUMN character_count SET DEFAULT 0,
    ALTER COLUMN character_count SET NOT NULL;
