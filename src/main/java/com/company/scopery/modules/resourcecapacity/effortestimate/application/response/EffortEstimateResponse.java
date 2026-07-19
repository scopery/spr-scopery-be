package com.company.scopery.modules.resourcecapacity.effortestimate.application.response;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimate;
import java.math.BigDecimal; import java.util.UUID;
public record EffortEstimateResponse(UUID id, UUID projectId, String targetType, UUID targetId, String estimateType,
        BigDecimal effortHours, String status) {
    public static EffortEstimateResponse from(EffortEstimate e) {
        return new EffortEstimateResponse(e.id(), e.projectId(), e.targetType(), e.targetId(), e.estimateType().name(), e.effortHours(), e.status().name());
    }
}
