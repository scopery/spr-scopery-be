package com.company.scopery.modules.trust.anonymization.application.response;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlan;
import java.time.Instant; import java.util.UUID;
public record AnonymizationPlanResponse(UUID id, UUID workspaceId, UUID dataSubjectIndexId,
        String status, String planJson, String dryRunResultJson, boolean legalHoldBlocked,
        String reason, Instant executedAt, Instant cancelledAt, Instant createdAt) {
    public static AnonymizationPlanResponse from(AnonymizationPlan p) {
        return new AnonymizationPlanResponse(p.id(), p.workspaceId(), p.dataSubjectIndexId(),
                p.status(), p.planJson(), p.dryRunResultJson(), p.legalHoldBlocked(),
                p.reason(), p.executedAt(), p.cancelledAt(), p.createdAt());
    }
}
