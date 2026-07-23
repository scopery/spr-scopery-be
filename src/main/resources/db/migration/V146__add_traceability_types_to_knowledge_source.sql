-- Extend knowledge_source.source_type to accept traceability entity types.
ALTER TABLE knowledge_source
    DROP CONSTRAINT ck_knowledge_source_type;

ALTER TABLE knowledge_source
    ADD CONSTRAINT ck_knowledge_source_type CHECK (
        source_type IN (
            'TASK',
            'DOCUMENT_VERSION',
            'MEETING_MINUTE',
            'NATIVE_DOCUMENT_CONTENT',
            'FUNCTIONAL_ITEM',
            'NON_FUNCTIONAL_ITEM',
            'APP_MODULE',
            'REQUIREMENT'
        )
    );
