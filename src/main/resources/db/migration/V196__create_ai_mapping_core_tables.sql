-- AI Mapping Run: tracks each user-triggered suggestion generation batch
CREATE TABLE ai_mapping_run
(
    id                  UUID         NOT NULL,
    project_id          UUID         NOT NULL,
    relation_type       VARCHAR(50)  NOT NULL,
    scope               VARCHAR(50)  NOT NULL,
    model_deployment_id UUID,
    prompt_key          VARCHAR(100) NOT NULL,
    prompt_version      INTEGER      NOT NULL DEFAULT 1,
    summary_version     INTEGER      NOT NULL DEFAULT 1,
    candidate_limit     INTEGER      NOT NULL DEFAULT 5,
    requested_by        UUID,
    started_at          TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    status              VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    source_count        INTEGER,
    suggestion_count    INTEGER,
    token_usage_json    TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),

    CONSTRAINT pk_ai_mapping_run PRIMARY KEY (id),
    CONSTRAINT ck_ai_mapping_run_status
        CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    CONSTRAINT ck_ai_mapping_run_relation_type
        CHECK (relation_type IN ('REQUIREMENT_TO_FUNCTION', 'FUNCTION_TO_USE_CASE', 'USE_CASE_TO_TEST_CASE')),
    CONSTRAINT ck_ai_mapping_run_scope
        CHECK (scope IN ('UNMAPPED', 'INCOMPLETE_COVERAGE', 'CHANGED', 'SELECTED'))
);

CREATE INDEX idx_ai_mapping_run_project ON ai_mapping_run (project_id);
CREATE INDEX idx_ai_mapping_run_status ON ai_mapping_run (project_id, status);

-- AI Mapping Suggestion: one record per (source, target) pair produced by a run
CREATE TABLE ai_mapping_suggestion
(
    id                   UUID         NOT NULL,
    run_id               UUID         NOT NULL,
    source_type          VARCHAR(50)  NOT NULL,
    source_id            UUID         NOT NULL,
    source_version       INTEGER,
    source_summary_hash  VARCHAR(64),
    target_type          VARCHAR(50),
    target_id            UUID,
    target_version       INTEGER,
    target_summary_hash  VARCHAR(64),
    relation_type        VARCHAR(50)  NOT NULL,
    rank                 INTEGER,
    retrieval_score      NUMERIC(6, 4),
    ai_score             NUMERIC(6, 4),
    final_score          NUMERIC(6, 4),
    second_best_score    NUMERIC(6, 4),
    score_margin         NUMERIC(6, 4),
    confidence_band      VARCHAR(20),
    decision             VARCHAR(20)  NOT NULL DEFAULT 'NO_MATCH',
    reason_codes_json    TEXT,
    evidence_json        TEXT,
    warnings_json        TEXT,
    coverage_json        TEXT,
    review_status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    reviewed_by          UUID,
    reviewed_at          TIMESTAMPTZ,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),

    CONSTRAINT pk_ai_mapping_suggestion PRIMARY KEY (id),
    CONSTRAINT fk_ai_mapping_suggestion_run FOREIGN KEY (run_id) REFERENCES ai_mapping_run (id),
    CONSTRAINT ck_ai_mapping_suggestion_decision
        CHECK (decision IN ('SUGGEST', 'AMBIGUOUS', 'NO_MATCH')),
    CONSTRAINT ck_ai_mapping_suggestion_review_status
        CHECK (review_status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'REPLACED'))
);

CREATE INDEX idx_ai_mapping_suggestion_run ON ai_mapping_suggestion (run_id);
CREATE INDEX idx_ai_mapping_suggestion_source ON ai_mapping_suggestion (source_type, source_id);
CREATE INDEX idx_ai_mapping_suggestion_review ON ai_mapping_suggestion (run_id, review_status);
CREATE INDEX idx_ai_mapping_suggestion_confidence ON ai_mapping_suggestion (confidence_band, review_status);
