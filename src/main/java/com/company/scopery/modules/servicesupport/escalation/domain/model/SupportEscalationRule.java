package com.company.scopery.modules.servicesupport.escalation.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportEscalationRule(UUID id, UUID workspaceId, UUID serviceProfileId, UUID queueId,
        String ruleCode, String name, String triggerType, boolean enabled, String status,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportEscalationRule create(UUID workspaceId, String ruleCode, String name, String triggerType) {
        Instant now = Instant.now();
        return new SupportEscalationRule(UUID.randomUUID(), workspaceId, null, null, ruleCode, name, triggerType,
                true, "ACTIVE", 0, now, now);
    }
    public SupportEscalationRule disable() {
        return new SupportEscalationRule(id, workspaceId, serviceProfileId, queueId, ruleCode, name, triggerType,
                false, "INACTIVE", version, createdAt, Instant.now());
    }
    public SupportEscalationRule enable() {
        return new SupportEscalationRule(id, workspaceId, serviceProfileId, queueId, ruleCode, name, triggerType,
                true, "ACTIVE", version, createdAt, Instant.now());
    }
}
