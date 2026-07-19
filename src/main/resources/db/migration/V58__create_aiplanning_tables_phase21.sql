-- Phase 21: AI-assisted project planning

CREATE TABLE IF NOT EXISTS ai_planning_run (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    actor_user_id UUID NOT NULL,
    agent_id UUID,
    agent_version_id UUID,
    prompt_template_id UUID,
    prompt_template_version_id UUID,
    model_deployment_id UUID,
    ai_execution_log_id UUID,
    run_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    input_summary_json JSONB,
    context_snapshot_id UUID,
    output_summary_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_ai_planning_run PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_run_project FOREIGN KEY (project_id) REFERENCES project_project(id),
    CONSTRAINT ck_ai_planning_run_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED','CANCELLED')),
    CONSTRAINT ck_ai_planning_run_type CHECK (run_type IN (
        'PROJECT_PLAN_DRAFT','TEMPLATE_RECOMMENDATION','TASK_ESTIMATE_SUGGESTION','COST_ROLE_SUGGESTION',
        'SCHEDULE_RISK_EXPLANATION','FINANCE_INSIGHT','QUOTE_PROPOSAL_DRAFT','CHANGE_REQUEST_DRAFT','GENERAL_PROJECT_ASSISTANT'))
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_run_project ON ai_planning_run(project_id);
CREATE INDEX IF NOT EXISTS idx_ai_planning_run_status ON ai_planning_run(status);

CREATE TABLE IF NOT EXISTS ai_planning_context_snapshot (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    actor_user_id UUID NOT NULL,
    context_type VARCHAR(100) NOT NULL,
    access_scope_json JSONB NOT NULL,
    included_sections_json JSONB NOT NULL,
    redaction_summary_json JSONB,
    context_payload_json JSONB NOT NULL,
    token_estimate INT,
    trace_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_ai_planning_context_snapshot PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_context_snapshot_project FOREIGN KEY (project_id) REFERENCES project_project(id)
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_context_snapshot_project ON ai_planning_context_snapshot(project_id);

CREATE TABLE IF NOT EXISTS ai_planning_suggestion (
    id UUID NOT NULL,
    planning_run_id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    suggestion_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    rationale TEXT,
    confidence_label VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    source_references_json JSONB,
    reviewed_at TIMESTAMPTZ,
    reviewed_by UUID,
    applied_at TIMESTAMPTZ,
    applied_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_ai_planning_suggestion PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_suggestion_run FOREIGN KEY (planning_run_id) REFERENCES ai_planning_run(id),
    CONSTRAINT fk_ai_planning_suggestion_project FOREIGN KEY (project_id) REFERENCES project_project(id),
    CONSTRAINT ck_ai_planning_suggestion_status CHECK (status IN (
        'GENERATED','UNDER_REVIEW','ACCEPTED','PARTIALLY_ACCEPTED','REJECTED','APPLIED','PARTIALLY_APPLIED','FAILED_TO_APPLY','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_suggestion_project ON ai_planning_suggestion(project_id);
CREATE INDEX IF NOT EXISTS idx_ai_planning_suggestion_run ON ai_planning_suggestion(planning_run_id);

CREATE TABLE IF NOT EXISTS ai_planning_suggestion_item (
    id UUID NOT NULL,
    suggestion_id UUID NOT NULL,
    project_id UUID NOT NULL,
    item_type VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id UUID,
    operation VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    proposed_payload_json JSONB NOT NULL,
    rationale TEXT,
    confidence_label VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    apply_action VARCHAR(150),
    apply_result_json JSONB,
    reviewed_at TIMESTAMPTZ,
    reviewed_by UUID,
    applied_at TIMESTAMPTZ,
    applied_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_ai_planning_suggestion_item PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_suggestion_item_suggestion FOREIGN KEY (suggestion_id) REFERENCES ai_planning_suggestion(id),
    CONSTRAINT ck_ai_planning_suggestion_item_status CHECK (status IN ('PROPOSED','ACCEPTED','REJECTED','APPLIED','FAILED','SKIPPED')),
    CONSTRAINT ck_ai_planning_suggestion_item_operation CHECK (operation IN ('CREATE','UPDATE','DELETE','ARCHIVE','MOVE','RECOMMEND','DRAFT_TEXT'))
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_suggestion_item_suggestion ON ai_planning_suggestion_item(suggestion_id);

CREATE TABLE IF NOT EXISTS ai_planning_review_action (
    id UUID NOT NULL,
    suggestion_id UUID NOT NULL,
    suggestion_item_id UUID,
    action VARCHAR(50) NOT NULL,
    actor_user_id UUID NOT NULL,
    reason TEXT,
    trace_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_ai_planning_review_action PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_review_action_suggestion FOREIGN KEY (suggestion_id) REFERENCES ai_planning_suggestion(id),
    CONSTRAINT ck_ai_planning_review_action_action CHECK (action IN (
        'START_REVIEW','ACCEPT','REJECT','ACCEPT_ITEM','REJECT_ITEM','APPLY','APPLY_ITEM','ARCHIVE'))
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_review_action_suggestion ON ai_planning_review_action(suggestion_id);

CREATE TABLE IF NOT EXISTS ai_planning_apply_result (
    id UUID NOT NULL,
    suggestion_id UUID NOT NULL,
    suggestion_item_id UUID,
    project_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    domain_action VARCHAR(150),
    target_type VARCHAR(100),
    target_id UUID,
    result_payload_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    created_by UUID,
    trace_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_ai_planning_apply_result PRIMARY KEY (id),
    CONSTRAINT fk_ai_planning_apply_result_suggestion FOREIGN KEY (suggestion_id) REFERENCES ai_planning_suggestion(id),
    CONSTRAINT ck_ai_planning_apply_result_status CHECK (status IN ('SUCCESS','FAILED','SKIPPED','ROLLED_BACK','CHANGE_REQUEST_REQUIRED'))
);
CREATE INDEX IF NOT EXISTS idx_ai_planning_apply_result_suggestion ON ai_planning_apply_result(suggestion_id);
