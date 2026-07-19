package com.company.scopery.modules.iam.auditevent.application.response;

import com.company.scopery.common.audit.ImmutableAuditEventJpaEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Immutable audit event record capturing a state-changing action in the system")
public record AuditEventResponse(
        @Schema(description = "Unique identifier of the audit event", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Type of audit event (e.g. USER_CREATED, ROLE_ASSIGNED)", example = "USER_CREATED")
        String eventType,

        @Schema(description = "Severity level of the event (e.g. INFO, WARN, CRITICAL)", example = "INFO")
        String severity,

        @Schema(description = "ID of the actor who performed the action", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID actorId,

        @Schema(description = "Type of actor (e.g. USER, SYSTEM, SERVICE)", example = "USER", nullable = true)
        String actorType,

        @Schema(description = "Type of the resource that was affected", example = "IAM_USER")
        String resourceType,

        @Schema(description = "Reference ID of the affected resource", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID resourceRefId,

        @Schema(description = "Organization ID associated with this event", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace ID associated with this event", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "JSON snapshot of the resource state before the event", example = "{\"status\":\"INACTIVE\"}", nullable = true)
        String beforeState,

        @Schema(description = "JSON snapshot of the resource state after the event", example = "{\"status\":\"ACTIVE\"}", nullable = true)
        String afterState,

        @Schema(description = "Human-readable reason or note for the event", example = "User activated by admin", nullable = true)
        String reason,

        @Schema(description = "Distributed trace ID linking the event to an HTTP request", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890", nullable = true)
        String traceId,

        @Schema(description = "Timestamp when the event occurred", example = "2026-07-17T03:00:00.000000Z")
        Instant occurredAt) {

    public static AuditEventResponse from(ImmutableAuditEventJpaEntity e) {
        return new AuditEventResponse(
                e.getId(), e.getEventType(), e.getSeverity(),
                e.getActorId(), e.getActorType(),
                e.getResourceType(), e.getResourceRefId(),
                e.getOrganizationId(), e.getWorkspaceId(),
                e.getBeforeState(), e.getAfterState(),
                e.getReason(), e.getTraceId(), e.getOccurredAt());
    }
}
