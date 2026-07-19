package com.company.scopery.modules.trust.sensitiveaccesslog.application.response;
import com.company.scopery.modules.trust.sensitiveaccesslog.domain.model.SensitiveAccessLog;
import java.time.Instant; import java.util.UUID;
public record SensitiveAccessLogResponse(UUID id, UUID workspaceId, String targetObjectType,
        UUID targetObjectId, String classification, String accessAction, Instant occurredAt) {
    public static SensitiveAccessLogResponse from(SensitiveAccessLog l) {
        return new SensitiveAccessLogResponse(l.id(), l.workspaceId(), l.targetObjectType(),
                l.targetObjectId(), l.classification(), l.accessAction(), l.occurredAt());
    }
}
