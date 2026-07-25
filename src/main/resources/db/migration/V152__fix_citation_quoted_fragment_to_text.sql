-- quoted_fragment was VARCHAR(2000), too short for large document chunks (BRD chunks can exceed 2000 chars).
ALTER TABLE aiassistant_message_citation ALTER COLUMN quoted_fragment TYPE TEXT;
