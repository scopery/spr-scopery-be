CREATE TABLE app_idempotency_key
(
    key_hash        VARCHAR(64)  NOT NULL,
    request_method  VARCHAR(20)  NOT NULL,
    request_path    VARCHAR(500) NOT NULL,
    response_status INT          NOT NULL,
    response_body   JSONB        NOT NULL,
    content_type    VARCHAR(200),
    expires_at      TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    CONSTRAINT pk_app_idempotency_key PRIMARY KEY (key_hash)
);

CREATE INDEX idx_app_idempotency_key_expires_at ON app_idempotency_key (expires_at);
