package com.company.scopery.modules.trust.anonymization.application.action;
import com.company.scopery.modules.trust.anonymization.application.response.AnonymizationPlanResponse;
import com.company.scopery.modules.trust.anonymization.application.service.AnonymizationGuardService;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlanRepository;
import com.company.scopery.modules.trust.shared.activity.TrustActivityLogger;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.constant.TrustActivityActions;
import com.company.scopery.modules.trust.shared.constant.TrustEntityTypes;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ExecuteAnonymizationPlanAction {
    private final AnonymizationPlanRepository repo;
    private final AnonymizationGuardService guard;
    private final TrustAuthorizationService auth;
    private final TrustActivityLogger activity;
    public ExecuteAnonymizationPlanAction(AnonymizationPlanRepository repo, AnonymizationGuardService guard,
            TrustAuthorizationService auth, TrustActivityLogger activity) {
        this.repo = repo; this.guard = guard; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public AnonymizationPlanResponse execute(UUID workspaceId, UUID planId) {
        auth.requireManage(workspaceId);
        guard.requireNoActiveLegalHold(workspaceId);
        var plan = repo.findById(planId).orElseThrow(() -> TrustExceptions.evidenceNotFound(planId));
        if (!workspaceId.equals(plan.workspaceId())) throw TrustExceptions.evidenceNotFound(planId);
        try {
            var saved = repo.save(plan.execute());
            activity.logSuccess(TrustEntityTypes.ANONYMIZATION_PLAN, saved.id(), TrustActivityActions.ANONYMIZATION_EXECUTED, "Anonymization plan executed");
            return AnonymizationPlanResponse.from(saved);
        } catch (IllegalStateException e) { throw TrustExceptions.dryRunRequired(); }
    }
}
