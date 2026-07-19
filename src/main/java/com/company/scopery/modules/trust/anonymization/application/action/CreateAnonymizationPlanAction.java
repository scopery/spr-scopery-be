package com.company.scopery.modules.trust.anonymization.application.action;
import com.company.scopery.modules.trust.anonymization.application.response.AnonymizationPlanResponse;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlan;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlanRepository;
import com.company.scopery.modules.trust.anonymization.application.service.AnonymizationGuardService;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateAnonymizationPlanAction {
    private final AnonymizationPlanRepository repo;
    private final AnonymizationGuardService guard;
    private final TrustAuthorizationService auth;
    public CreateAnonymizationPlanAction(AnonymizationPlanRepository repo, AnonymizationGuardService guard, TrustAuthorizationService auth) {
        this.repo = repo; this.guard = guard; this.auth = auth;
    }
    @Transactional
    public AnonymizationPlanResponse execute(UUID workspaceId, UUID dataSubjectIndexId, String planJson, String reason) {
        auth.requireManage(workspaceId);
        guard.requireNoActiveLegalHold(workspaceId);
        var saved = repo.save(AnonymizationPlan.create(workspaceId, dataSubjectIndexId, planJson, reason));
        return AnonymizationPlanResponse.from(saved);
    }
}
