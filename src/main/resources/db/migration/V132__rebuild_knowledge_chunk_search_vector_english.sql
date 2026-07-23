-- Rebuild search_vector using 'english' dictionary instead of 'simple'.
-- English config removes stop words (what, is, the, about, ...) and applies stemming,
-- so natural-language questions match document content correctly.
UPDATE knowledge_chunk
SET search_vector = to_tsvector('english', COALESCE(title, '') || ' ' || COALESCE(plain_text, ''))
WHERE plain_text IS NOT NULL;
