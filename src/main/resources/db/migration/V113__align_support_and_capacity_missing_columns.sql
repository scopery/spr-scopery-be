-- Align support + capacity tables with JPA entities (missing columns from slim V85/V94 creates).

-- support_work_link: AuditableJpaEntity.updated_by
ALTER TABLE support_work_link
    ADD COLUMN IF NOT EXISTS updated_by character varying(255);

-- support_knowledge_link: AuditableJpaEntity.updated_by
ALTER TABLE support_knowledge_link
    ADD COLUMN IF NOT EXISTS updated_by character varying(255);

-- support_request_type: description + enabled (entity has both; V85 slim table omitted them)
ALTER TABLE support_request_type
    ADD COLUMN IF NOT EXISTS description text;
ALTER TABLE support_request_type
    ADD COLUMN IF NOT EXISTS enabled boolean NOT NULL DEFAULT true;

-- resource_skill: default_rate_card_id (present on resource_role, missing on skill)
ALTER TABLE resource_skill
    ADD COLUMN IF NOT EXISTS default_rate_card_id uuid;
