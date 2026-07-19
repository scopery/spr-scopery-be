package com.company.scopery.modules.resourcecapacity.utilization.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.resourcecapacity.planning.application.response.UtilizationSummaryResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.service.ResourcePlanningRebuildService;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class RebuildUtilizationSummaryAction {
    private final ResourcePlanningRebuildService rebuild;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public RebuildUtilizationSummaryAction(ResourcePlanningRebuildService rebuild,
                                           CapacityWorkspaceAuthorizationService auth,
                                           CapacityActivityLogger activity) {
        this.rebuild = rebuild;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public UtilizationSummaryResponse execute(UUID workspaceId, UUID resourceProfileId,
                                              BigDecimal effortHours, BigDecimal availableCapacityHours) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_VIEW);
        var policy = rebuild.resolveThreshold(workspaceId, null);
        var result = rebuild.computeUtilization(resourceProfileId,
                availableCapacityHours != null ? availableCapacityHours : BigDecimal.ZERO,
                effortHours != null ? effortHours : BigDecimal.ZERO,
                policy);
        activity.logSuccess(CapacityEntityTypes.RESOURCE_UTILIZATION_SUMMARY, resourceProfileId,
                CapacityActivityActions.UTILIZATION_REBUILT, "Utilization rebuilt for resource " + resourceProfileId);
        return result;
    }
}
