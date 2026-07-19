package com.company.scopery.modules.trust.sensitiveaccesslog.domain.model;
import java.time.Instant; import java.util.UUID;
public record SensitiveAccessLog(UUID id, UUID workspaceId, UUID projectId,
        String actorPrincipalType, UUID actorUserId, String targetObjectType, UUID targetObjectId,
        String fieldPath, String classification, String accessAction, String accessChannel,
        String reason, String requestPath, Instant occurredAt, String traceId,
        int version, Instant createdAt) {
    public static SensitiveAccessLog record(UUID workspaceId, UUID projectId, String actorPrincipalType,
            UUID actorUserId, String targetObjectType, UUID targetObjectId, String fieldPath,
            String classification, String accessAction, String accessChannel, String reason,
            String requestPath, String traceId) {
        Instant now = Instant.now();
        return new SensitiveAccessLog(UUID.randomUUID(), workspaceId, projectId, actorPrincipalType,
                actorUserId, targetObjectType, targetObjectId, fieldPath, classification,
                accessAction, accessChannel, reason, requestPath, now, traceId, 0, now);
    }
}
