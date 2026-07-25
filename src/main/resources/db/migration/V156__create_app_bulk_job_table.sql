CREATE TABLE app_bulk_job (
    id           UUID         NOT NULL,
    job_type     VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'QUEUED',
    actor_username VARCHAR(255),
    total_items  INT          NOT NULL DEFAULT 0,
    succeeded_items INT       NOT NULL DEFAULT 0,
    failed_items INT          NOT NULL DEFAULT 0,
    payload_json TEXT         NOT NULL,
    result_summary TEXT,
    error_message TEXT,
    leased_by    VARCHAR(100),
    leased_at    TIMESTAMPTZ,
    lease_until  TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    CONSTRAINT pk_app_bulk_job PRIMARY KEY (id)
);

CREATE INDEX idx_app_bulk_job_status ON app_bulk_job (status);
