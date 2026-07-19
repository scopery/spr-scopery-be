package com.company.scopery.modules.profitability.profile.application.service;
import com.company.scopery.modules.profitability.profile.application.response.*;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class ProfitabilityQueryService {
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization;
    private final ProfitabilitySummaryRebuildService rebuildService;
    public ProfitabilityQueryService(ProjectProfitabilityProfileRepository profiles, ProfitabilityAuthorizationService authorization,
                                     ProfitabilitySummaryRebuildService rebuildService) {
        this.profiles=profiles; this.authorization=authorization; this.rebuildService=rebuildService;
    }
    @Transactional(readOnly=true)
    public ProjectProfitabilityProfileResponse getProfile(UUID projectId) {
        authorization.requireView(projectId);
        return ProjectProfitabilityProfileResponse.from(profiles.findByProjectId(projectId).orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId)));
    }
    @Transactional(readOnly=true)
    public ProfitabilitySummaryResponse summary(UUID projectId) {
        authorization.requireSummary(projectId);
        var profile = profiles.findByProjectId(projectId).orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        return rebuildService.rebuild(profile);
    }

    @Transactional(readOnly = true)
    public ProfitabilityPortalSummaryResponse portalSummary(UUID projectId) {
        authorization.requireSummary(projectId);
        var profile = profiles.findByProjectId(projectId).orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        if (profile.portalVisibility() == null || "NONE".equalsIgnoreCase(profile.portalVisibility())) {
            throw ProfitabilityExceptions.portalSummaryNotVisible(projectId);
        }
        ProfitabilitySummaryResponse summary = rebuildService.rebuild(profile);
        boolean full = "FULL".equalsIgnoreCase(profile.portalVisibility());
        return new ProfitabilityPortalSummaryResponse(
                summary.currency(),
                summary.profitabilityStatus(),
                summary.forecastMarginPercent(),
                true,
                full,
                full ? summary.forecastRevenue() : null,
                full ? summary.forecastCost() : null,
                full ? summary.forecastProfit() : null);
    }
}
