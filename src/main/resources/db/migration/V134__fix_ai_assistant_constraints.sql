-- 1. Add PROJECT_FULL_CONTEXT to knowledge_retrieval_trace allowed modes
ALTER TABLE knowledge_retrieval_trace
    DROP CONSTRAINT ck_knowledge_retrieval_mode;

ALTER TABLE knowledge_retrieval_trace
    ADD CONSTRAINT ck_knowledge_retrieval_mode
        CHECK (retrieval_mode IN ('LEXICAL', 'VECTOR', 'HYBRID_RRF', 'PROJECT_FULL_CONTEXT'));

-- 2. Add knowledge source types to aiassistant_message_citation allowed source types
ALTER TABLE aiassistant_message_citation
    DROP CONSTRAINT ck_aiasst_cit_source_type;

ALTER TABLE aiassistant_message_citation
    ADD CONSTRAINT ck_aiasst_cit_source_type
        CHECK (source_type IN (
            'TASK',
            'DOCUMENT_VERSION',
            'MEETING_MINUTE',
            'NATIVE_DOCUMENT_CONTENT',
            'SYNCED_BLOCK',
            'DOCUMENT_TEMPLATE_VERSION'
        ));
