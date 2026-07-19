package com.company.scopery.modules.trust.anonymization.application.action;
import com.company.scopery.modules.trust.anonymization.application.response.AnonymizationPlanResponse;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlanRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DryRunAnonymizationPlanAction {
    private final AnonymizationPlanRepository repo;
    private final TrustAuthorizationService auth;
    public DryRunAnonymizationPlanAction(AnonymizationPlanRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public AnonymizationPlanResponse execute(UUID workspaceId, UUID planId) {
        auth.requireManage(workspaceId);
        var plan = repo.findById(planId).orElseThrow(() -> TrustExceptions.evidenceNotFound(planId));
        if (!workspaceId.equals(plan.workspaceId())) throw TrustExceptions.evidenceNotFound(planId);
        return AnonymizationPlanResponse.from(repo.save(plan.completeDryRun("DRY_RUN_STUB_RESULT")));
    }
}
