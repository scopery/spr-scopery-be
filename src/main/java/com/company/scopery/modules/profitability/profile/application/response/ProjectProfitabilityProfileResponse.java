package com.company.scopery.modules.profitability.profile.application.response;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfile;
import java.util.UUID;
public record ProjectProfitabilityProfileResponse(UUID id, UUID projectId, String currency, String status, String trackingMode, String revenueMode, String costMode) {
    public static ProjectProfitabilityProfileResponse from(ProjectProfitabilityProfile p) {
        return new ProjectProfitabilityProfileResponse(p.id(), p.projectId(), p.currency(), p.status(), p.trackingMode(), p.revenueMode(), p.costMode());
    }
}
