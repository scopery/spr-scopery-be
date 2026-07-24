-- Phase 44: Add action.plan_ready to allowed SSE stream event types
ALTER TABLE aiassistant_stream_event DROP CONSTRAINT ck_aiasst_stream_type;

ALTER TABLE aiassistant_stream_event ADD CONSTRAINT ck_aiasst_stream_type CHECK (event_type IN (
    'message.started','context.completed','retrieval.started','retrieval.completed',
    'answer.delta','citation.added','answer.completed','answer.cancelled',
    'answer.failed','answer.blocked','heartbeat',
    'action.plan_ready'
));
