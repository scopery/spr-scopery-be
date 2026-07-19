package com.company.scopery.modules.profitability.profile.application.action;
import com.company.scopery.modules.profitability.profile.application.response.ProfitabilitySummaryResponse;
import com.company.scopery.modules.profitability.profile.application.service.ProfitabilitySummaryRebuildService;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.activity.ProfitabilityActivityLogger;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RebuildProfitabilitySummaryAction {
    private final ProjectProfitabilityProfileRepository profiles; private final ProfitabilitySummaryRebuildService rebuildService;
    private final ProfitabilityAuthorizationService authorization; private final ProfitabilityActivityLogger activityLogger;
    public RebuildProfitabilitySummaryAction(ProjectProfitabilityProfileRepository profiles, ProfitabilitySummaryRebuildService rebuildService,
                                             ProfitabilityAuthorizationService authorization, ProfitabilityActivityLogger activityLogger) {
        this.profiles=profiles; this.rebuildService=rebuildService; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional(readOnly=true)
    public ProfitabilitySummaryResponse execute(UUID projectId) {
        authorization.requireSummary(projectId);
        var profile = profiles.findByProjectId(projectId).orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        var summary = rebuildService.rebuild(profile);
        activityLogger.logSuccess("PROFITABILITY_SUMMARY", profile.id(), "PROFITABILITY_SUMMARY_REBUILT", "Summary rebuilt from quote/finance");
        return summary;
    }
}
