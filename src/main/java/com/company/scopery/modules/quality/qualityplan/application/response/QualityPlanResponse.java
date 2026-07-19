package com.company.scopery.modules.quality.qualityplan.application.response;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlan;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record QualityPlanResponse(UUID id, UUID projectId, String code, String name, String status, boolean currentFlag, Instant createdAt) {
    public static QualityPlanResponse from(QualityPlan e) {
        return new QualityPlanResponse(e.id(), e.projectId(), e.code(), e.name(), e.status().name(), e.currentFlag(), e.createdAt());
    }
}
