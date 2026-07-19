-- Phase 18: Quote / Commercial Proposal foundation
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_quote_id UUID;
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_quote_version_id UUID;

CREATE TABLE IF NOT EXISTS quote_quote (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    source_finance_scenario_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    client_name VARCHAR(255),
    client_company VARCHAR(255),
    client_email VARCHAR(255),
    client_contact_name VARCHAR(255),
    client_reference VARCHAR(255),
    external_party_id UUID,
    status VARCHAR(50) NOT NULL,
    current_version_id UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_quote_quote PRIMARY KEY (id),
    CONSTRAINT fk_quote_quote_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT fk_quote_quote_finance_scenario FOREIGN KEY (source_finance_scenario_id)
        REFERENCES project_finance_scenario(id),
    CONSTRAINT uq_quote_quote_project_code UNIQUE (project_id, code),
    CONSTRAINT ck_quote_quote_status CHECK (status IN ('DRAFT','ACTIVE','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_quote_quote_project ON quote_quote(project_id);
CREATE INDEX IF NOT EXISTS idx_quote_quote_workspace ON quote_quote(workspace_id);
CREATE INDEX IF NOT EXISTS idx_quote_quote_status ON quote_quote(status);
CREATE INDEX IF NOT EXISTS idx_quote_quote_source_finance ON quote_quote(source_finance_scenario_id);

CREATE TABLE IF NOT EXISTS quote_version (
    id UUID NOT NULL,
    quote_id UUID NOT NULL,
    project_id UUID NOT NULL,
    finance_scenario_id UUID NOT NULL,
    version_number INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    title_snapshot VARCHAR(255) NOT NULL,
    client_snapshot_json JSONB,
    finance_snapshot_json JSONB NOT NULL,
    formula_version VARCHAR(50) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    target_margin_percent DECIMAL(8,4),
    pricing_method VARCHAR(50) NOT NULL,
    cost_base_method VARCHAR(50) NOT NULL,
    current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    discount_method VARCHAR(50) NOT NULL DEFAULT 'NONE',
    discount_percent DECIMAL(8,4),
    discount_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    discount_reason TEXT,
    valid_until DATE,
    proposal_title VARCHAR(255),
    proposal_notes TEXT,
    submitted_at TIMESTAMPTZ,
    submitted_by UUID,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    sent_at TIMESTAMPTZ,
    sent_by UUID,
    accepted_at TIMESTAMPTZ,
    accepted_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_quote_version PRIMARY KEY (id),
    CONSTRAINT fk_quote_version_quote FOREIGN KEY (quote_id)
        REFERENCES quote_quote(id) ON DELETE CASCADE,
    CONSTRAINT fk_quote_version_finance_scenario FOREIGN KEY (finance_scenario_id)
        REFERENCES project_finance_scenario(id),
    CONSTRAINT uq_quote_version_quote_number UNIQUE (quote_id, version_number),
    CONSTRAINT ck_quote_version_status CHECK (status IN (
        'DRAFT','SUBMITTED','APPROVED','REJECTED','SENT','ACCEPTED','ARCHIVED')),
    CONSTRAINT ck_quote_version_pricing_method CHECK (pricing_method IN (
        'FROM_FINANCE_PLANNED_REVENUE','TARGET_MARGIN_SOLVER','MANUAL_TOTAL','PHASE_LINE_SUM')),
    CONSTRAINT ck_quote_version_cost_base_method CHECK (cost_base_method IN (
        'BUDGET_OF_COSTS','DIRECT_COST','CUSTOM')),
    CONSTRAINT ck_quote_version_discount_method CHECK (discount_method IN (
        'NONE','FIXED_AMOUNT','PERCENT_OF_SUBTOTAL'))
);
CREATE INDEX IF NOT EXISTS idx_quote_version_quote ON quote_version(quote_id);
CREATE INDEX IF NOT EXISTS idx_quote_version_project ON quote_version(project_id);
CREATE INDEX IF NOT EXISTS idx_quote_version_status ON quote_version(status);
CREATE INDEX IF NOT EXISTS idx_quote_version_finance ON quote_version(finance_scenario_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_quote_version_current
    ON quote_version(quote_id) WHERE current_flag = TRUE;

CREATE TABLE IF NOT EXISTS quote_line (
    id UUID NOT NULL,
    quote_version_id UUID NOT NULL,
    project_id UUID NOT NULL,
    source_phase_finance_id UUID,
    source_project_phase_id UUID,
    line_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    quantity DECIMAL(18,4) NOT NULL DEFAULT 1,
    unit_price DECIMAL(18,4) NOT NULL DEFAULT 0,
    amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    currency_code VARCHAR(10) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    client_visible BOOLEAN NOT NULL DEFAULT TRUE,
    internal_note TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_quote_line PRIMARY KEY (id),
    CONSTRAINT fk_quote_line_version FOREIGN KEY (quote_version_id)
        REFERENCES quote_version(id) ON DELETE CASCADE,
    CONSTRAINT ck_quote_line_type CHECK (line_type IN (
        'PHASE','SERVICE','DELIVERABLE','CUSTOM','OPTIONAL')),
    CONSTRAINT ck_quote_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_quote_line_unit_price CHECK (unit_price >= 0)
);
CREATE INDEX IF NOT EXISTS idx_quote_line_version ON quote_line(quote_version_id);
CREATE INDEX IF NOT EXISTS idx_quote_line_project ON quote_line(project_id);

CREATE TABLE IF NOT EXISTS quote_summary (
    id UUID NOT NULL,
    quote_version_id UUID NOT NULL,
    project_id UUID NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    cost_base DECIMAL(18,4) NOT NULL DEFAULT 0,
    direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    overhead DECIMAL(18,4) NOT NULL DEFAULT 0,
    subtotal_before_discount DECIMAL(18,4) NOT NULL DEFAULT 0,
    discount_method VARCHAR(50) NOT NULL DEFAULT 'NONE',
    discount_percent DECIMAL(8,4),
    discount_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    subtotal_after_discount DECIMAL(18,4) NOT NULL DEFAULT 0,
    tax_mode VARCHAR(50) NOT NULL DEFAULT 'TAX_EXCLUDED',
    tax_amount DECIMAL(18,4),
    total_quoted_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    target_margin_percent DECIMAL(8,4),
    required_contract_value DECIMAL(18,4),
    gross_margin DECIMAL(18,4),
    gross_margin_percent DECIMAL(8,4),
    profit_before_tax DECIMAL(18,4),
    pbt_percent DECIMAL(8,4),
    formula_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_quote_summary PRIMARY KEY (id),
    CONSTRAINT fk_quote_summary_version FOREIGN KEY (quote_version_id)
        REFERENCES quote_version(id) ON DELETE CASCADE,
    CONSTRAINT uq_quote_summary_version UNIQUE (quote_version_id),
    CONSTRAINT ck_quote_summary_discount_method CHECK (discount_method IN (
        'NONE','FIXED_AMOUNT','PERCENT_OF_SUBTOTAL')),
    CONSTRAINT ck_quote_summary_tax_mode CHECK (tax_mode IN (
        'TAX_EXCLUDED','TAX_INCLUDED_DISPLAY_ONLY','TAX_NOT_APPLICABLE'))
);
CREATE INDEX IF NOT EXISTS idx_quote_summary_project ON quote_summary(project_id);

CREATE TABLE IF NOT EXISTS quote_term (
    id UUID NOT NULL,
    quote_version_id UUID NOT NULL,
    project_id UUID NOT NULL,
    term_type VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    content TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    client_visible BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_quote_term PRIMARY KEY (id),
    CONSTRAINT fk_quote_term_version FOREIGN KEY (quote_version_id)
        REFERENCES quote_version(id) ON DELETE CASCADE,
    CONSTRAINT ck_quote_term_type CHECK (term_type IN (
        'PAYMENT_TERM','VALIDITY','ASSUMPTION','EXCLUSION','DELIVERY_TERM',
        'WARRANTY','SUPPORT_TERM','LEGAL_NOTE','TAX_NOTE'))
);
CREATE INDEX IF NOT EXISTS idx_quote_term_version ON quote_term(quote_version_id);
CREATE INDEX IF NOT EXISTS idx_quote_term_project ON quote_term(project_id);
