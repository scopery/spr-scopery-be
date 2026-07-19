package com.company.scopery.modules.resourcecapacity.planning.application.action;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceRiskFlagResponse;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class MitigateResourceRiskFlagAction {
    private final ResourceRiskFlagRepository repo; private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public MitigateResourceRiskFlagAction(ResourceRiskFlagRepository repo, CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ResourceRiskFlagResponse execute(UUID projectId, UUID riskFlagId) {
        var flag = repo.findById(riskFlagId).orElseThrow(() -> CapacityExceptions.riskFlagNotFound(riskFlagId));
        if (flag.projectId() == null || !flag.projectId().equals(projectId)) throw CapacityExceptions.riskFlagNotFound(riskFlagId);
        auth.requireWorkspacePermission(flag.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        try {
            var saved = repo.save(flag.mitigate());
            activity.logSuccess(CapacityEntityTypes.RESOURCE_RISK_FLAG, saved.id(), CapacityActivityActions.RESOURCE_RISK_FLAG_MITIGATED, "Mitigated");
            return new ResourceRiskFlagResponse(saved.id(), saved.projectId(), saved.riskReason(), saved.impactType(), saved.status(), saved.description());
        } catch (IllegalStateException ex) {
            throw CapacityExceptions.riskFlagInvalidStatus();
        }
    }
}
