package com.company.scopery.modules.resourcecapacity.risk.domain.model;
import java.time.Instant; import java.util.UUID;
public record ResourceRiskFlag(UUID id, UUID workspaceId, UUID projectId, UUID resourceProfileId,
        String riskReason, String impactType, String description, String status,
        Instant mitigatedAt, Instant closedAt, int version, Instant createdAt, Instant updatedAt) {
    public static ResourceRiskFlag open(UUID workspaceId, UUID projectId, UUID resourceProfileId,
                                        String reason, String impactType, String description) {
        Instant now = Instant.now();
        return new ResourceRiskFlag(UUID.randomUUID(), workspaceId, projectId, resourceProfileId,
                reason, impactType, description, "OPEN", null, null, 0, now, now);
    }
    public ResourceRiskFlag mitigate() {
        if (!"OPEN".equals(status)) throw new IllegalStateException("Only OPEN can be mitigated");
        return new ResourceRiskFlag(id, workspaceId, projectId, resourceProfileId, riskReason, impactType, description,
                "MITIGATED", Instant.now(), closedAt, version, createdAt, Instant.now());
    }
    public ResourceRiskFlag close() {
        if ("CLOSED".equals(status) || "ARCHIVED".equals(status)) throw new IllegalStateException("Already closed");
        return new ResourceRiskFlag(id, workspaceId, projectId, resourceProfileId, riskReason, impactType, description,
                "CLOSED", mitigatedAt, Instant.now(), version, createdAt, Instant.now());
    }
}
