package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.time.Instant; import java.util.UUID;
public record SlaTarget(UUID id, UUID workspaceId, UUID slaPolicyId, UUID requestTypeId, String priority,
        String targetType, int durationMinutes, boolean enabled,
        int version, Instant createdAt, Instant updatedAt) {
    public static SlaTarget create(UUID workspaceId, UUID slaPolicyId, String targetType, int durationMinutes) {
        Instant now = Instant.now();
        return new SlaTarget(UUID.randomUUID(), workspaceId, slaPolicyId, null, null,
                targetType, durationMinutes, true, 0, now, now);
    }
    public SlaTarget disable() {
        return new SlaTarget(id, workspaceId, slaPolicyId, requestTypeId, priority, targetType, durationMinutes,
                false, version, createdAt, Instant.now());
    }
    public SlaTarget enable() {
        return new SlaTarget(id, workspaceId, slaPolicyId, requestTypeId, priority, targetType, durationMinutes,
                true, version, createdAt, Instant.now());
    }
}
