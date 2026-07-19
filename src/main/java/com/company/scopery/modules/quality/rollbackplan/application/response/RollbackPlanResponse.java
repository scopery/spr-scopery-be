package com.company.scopery.modules.quality.rollbackplan.application.response;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlan;
import java.time.Instant; import java.util.UUID;
public record RollbackPlanResponse(UUID id, UUID projectId, UUID releasePackageId, String title, String status, Instant approvedAt, Instant createdAt) {
    public static RollbackPlanResponse from(RollbackPlan e) {
        return new RollbackPlanResponse(e.id(), e.projectId(), e.releasePackageId(), e.title(), e.status().name(), e.approvedAt(), e.createdAt());
    }
}
