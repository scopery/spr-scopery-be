-- Align external_organization check constraints with TO-BE spec (Phase 29)
-- OrganizationStatus: add ON_HOLD, BLACKLISTED
-- OrganizationType: add PROSPECT, SUBCONTRACTOR, CONSULTANT, GOVERNMENT, PAYMENT_PROVIDER, TECHNOLOGY_PROVIDER

ALTER TABLE external_organization DROP CONSTRAINT ck_external_organization_status;
ALTER TABLE external_organization ADD CONSTRAINT ck_external_organization_status
    CHECK (status IN ('ACTIVE','INACTIVE','ON_HOLD','BLACKLISTED','ARCHIVED'));

ALTER TABLE external_organization DROP CONSTRAINT ck_external_organization_type;
ALTER TABLE external_organization ADD CONSTRAINT ck_external_organization_type
    CHECK (organization_type IN ('CLIENT','PROSPECT','VENDOR','PARTNER','SUBCONTRACTOR','CONSULTANT','REGULATOR','GOVERNMENT','PAYMENT_PROVIDER','TECHNOLOGY_PROVIDER','OTHER'));
