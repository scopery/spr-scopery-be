package com.company.scopery.modules.resourcecapacity.planning.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceRiskFlagResponse;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CloseResourceRiskFlagAction {
    private final ResourceRiskFlagRepository repo;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public CloseResourceRiskFlagAction(ResourceRiskFlagRepository repo,
                                       CapacityWorkspaceAuthorizationService auth,
                                       CapacityActivityLogger activity) {
        this.repo = repo;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public ResourceRiskFlagResponse execute(UUID projectId, UUID riskFlagId) {
        var flag = repo.findById(riskFlagId).orElseThrow(() -> CapacityExceptions.riskFlagNotFound(riskFlagId));
        if (flag.projectId() == null || !flag.projectId().equals(projectId)) {
            throw CapacityExceptions.riskFlagNotFound(riskFlagId);
        }
        auth.requireWorkspacePermission(flag.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        try {
            var saved = repo.save(flag.close());
            activity.logSuccess(CapacityEntityTypes.RESOURCE_RISK_FLAG, saved.id(),
                    CapacityActivityActions.RESOURCE_RISK_FLAG_CLOSED, "Risk flag closed");
            return ResourceRiskFlagResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw CapacityExceptions.riskFlagInvalidStatus();
        }
    }
}
