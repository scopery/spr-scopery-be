-- Phase 29 D1-D4: address, channel, communication preference, document link tables

CREATE TABLE IF NOT EXISTS external_party_address (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    external_organization_id UUID,
    external_contact_id UUID,
    address_type VARCHAR(50) NOT NULL,
    line1 VARCHAR(500),
    line2 VARCHAR(500),
    city VARCHAR(150),
    state_region VARCHAR(150),
    postal_code VARCHAR(20),
    country_code VARCHAR(10),
    primary_flag BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_external_party_address PRIMARY KEY (id),
    CONSTRAINT ck_external_party_address_type CHECK (address_type IN ('BILLING','SHIPPING','OFFICE','REGISTERED','PROJECT_SITE','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_external_party_address_org ON external_party_address(workspace_id, external_organization_id) WHERE external_organization_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_external_party_address_contact ON external_party_address(workspace_id, external_contact_id) WHERE external_contact_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS external_contact_channel (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    external_contact_id UUID NOT NULL,
    channel_type VARCHAR(50) NOT NULL,
    channel_value VARCHAR(500) NOT NULL,
    primary_flag BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_external_contact_channel PRIMARY KEY (id),
    CONSTRAINT ck_external_contact_channel_type CHECK (channel_type IN ('EMAIL','PHONE','MOBILE','WHATSAPP','TELEGRAM','WECHAT','LINE','SLACK_CONNECT','TEAMS_CONNECT','LINKEDIN','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_external_contact_channel_contact ON external_contact_channel(workspace_id, external_contact_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_external_contact_channel_primary ON external_contact_channel(workspace_id, external_contact_id, channel_type) WHERE primary_flag = TRUE;

CREATE TABLE IF NOT EXISTS external_communication_preference (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    external_organization_id UUID,
    external_contact_id UUID,
    preferred_channel_type VARCHAR(50),
    preferred_language VARCHAR(10),
    do_not_contact BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_external_communication_preference PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_external_comm_pref_org ON external_communication_preference(workspace_id, external_organization_id) WHERE external_organization_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_external_comm_pref_contact ON external_communication_preference(workspace_id, external_contact_id) WHERE external_contact_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS external_party_document_link (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    external_organization_id UUID,
    external_contact_id UUID,
    document_id UUID NOT NULL,
    link_note TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_external_party_document_link PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_external_party_doc_link_org ON external_party_document_link(workspace_id, external_organization_id) WHERE external_organization_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_external_party_doc_link_contact ON external_party_document_link(workspace_id, external_contact_id) WHERE external_contact_id IS NOT NULL;
