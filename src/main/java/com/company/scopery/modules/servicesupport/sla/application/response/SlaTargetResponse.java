package com.company.scopery.modules.servicesupport.sla.application.response;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTarget;
import java.time.Instant; import java.util.UUID;
public record SlaTargetResponse(UUID id, UUID workspaceId, UUID slaPolicyId, UUID requestTypeId, String priority,
        String targetType, int durationMinutes, boolean enabled, Instant createdAt) {
    public static SlaTargetResponse from(SlaTarget d) {
        return new SlaTargetResponse(d.id(), d.workspaceId(), d.slaPolicyId(), d.requestTypeId(), d.priority(),
                d.targetType(), d.durationMinutes(), d.enabled(), d.createdAt());
    }
}
