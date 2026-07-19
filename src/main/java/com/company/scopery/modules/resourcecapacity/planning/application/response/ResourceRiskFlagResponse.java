package com.company.scopery.modules.resourcecapacity.planning.application.response;

import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlag;

import java.util.UUID;

public record ResourceRiskFlagResponse(UUID id, UUID projectId, String riskReason, String impactType, String status, String description) {
    public static ResourceRiskFlagResponse from(ResourceRiskFlag f) {
        return new ResourceRiskFlagResponse(f.id(), f.projectId(), f.riskReason(), f.impactType(), f.status(), f.description());
    }
}
