package com.company.scopery.modules.servicesupport.sla.application.response;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicy;
import java.time.Instant; import java.util.UUID;
public record SlaPolicyResponse(UUID id, UUID workspaceId, String policyCode, String name,
        Integer firstResponseMinutes, Integer resolveMinutes, String status, Instant createdAt) {
    public static SlaPolicyResponse from(SlaPolicy p) {
        return new SlaPolicyResponse(p.id(), p.workspaceId(), p.policyCode(), p.name(),
                p.firstResponseMinutes(), p.resolveMinutes(), p.status(), p.createdAt());
    }
}
