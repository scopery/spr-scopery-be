-- Phase 42 durable SSE replay and daily quota accounting.

CREATE TABLE aiassistant_stream_event (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    sequence BIGINT NOT NULL,
    event_type VARCHAR(48) NOT NULL,
    payload JSONB NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_aiasst_stream_sequence CHECK (sequence > 0 AND sequence <= 4096),
    CONSTRAINT ck_aiasst_stream_type CHECK (event_type IN (
        'message.started','context.completed','retrieval.started','retrieval.completed',
        'answer.delta','citation.added','answer.completed','answer.cancelled',
        'answer.failed','answer.blocked','heartbeat'
    )),
    CONSTRAINT ck_aiasst_stream_payload CHECK (jsonb_typeof(payload) = 'object'),
    CONSTRAINT ck_aiasst_stream_hash CHECK (payload_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT uq_aiasst_stream_sequence UNIQUE (message_id, sequence)
);

CREATE INDEX ix_aiasst_stream_replay
    ON aiassistant_stream_event(message_id, sequence);
CREATE INDEX ix_aiasst_stream_expiry
    ON aiassistant_stream_event(expires_at);

CREATE TABLE aiassistant_active_stream (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    workspace_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    stream_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    acquired_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    released_at TIMESTAMPTZ NULL,
    CONSTRAINT ck_aiasst_active_stream_status CHECK (stream_status IN ('ACTIVE','RELEASED','EXPIRED')),
    CONSTRAINT uq_aiasst_active_stream_message UNIQUE (message_id)
);

CREATE INDEX ix_aiasst_active_stream_actor
    ON aiassistant_active_stream(workspace_id, actor_id, stream_status, expires_at);
CREATE INDEX ix_aiasst_active_stream_expiry
    ON aiassistant_active_stream(expires_at, stream_status);

CREATE TABLE aiassistant_quota_usage (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    usage_date DATE NOT NULL,
    turn_count INTEGER NOT NULL DEFAULT 0,
    input_token_count BIGINT NOT NULL DEFAULT 0,
    output_token_count BIGINT NOT NULL DEFAULT 0,
    failed_turn_count INTEGER NOT NULL DEFAULT 0,
    blocked_turn_count INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_quota_counts CHECK (
        turn_count >= 0
        AND input_token_count >= 0
        AND output_token_count >= 0
        AND failed_turn_count >= 0
        AND blocked_turn_count >= 0
    ),
    CONSTRAINT uq_aiasst_quota_day UNIQUE (workspace_id, actor_id, usage_date)
);

CREATE INDEX ix_aiasst_quota_actor_date
    ON aiassistant_quota_usage(actor_id, usage_date DESC);
