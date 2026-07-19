-- Align deferred entity tables with AuditableJpaEntity (updated_at / updated_by)
ALTER TABLE project_test_case_coverage ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE project_test_case_coverage ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE project_defect_link ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE project_defect_link ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE project_release_item ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE project_release_item ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE document_link ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE document_link ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE traceability_link ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE traceability_link ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE app_registry_api_endpoint ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE app_registry_api_endpoint ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE app_registry_project_mapping ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE app_registry_project_mapping ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE project_external_party_relationship ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE project_external_party_relationship ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE project_approval_authority ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE project_approval_authority ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE external_portal_invite ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE external_portal_invite ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE client_review_decision ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE client_review_decision ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE client_comment ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE client_comment ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE document_template_version ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE document_template_version ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

ALTER TABLE document_access_log ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE document_access_log ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
