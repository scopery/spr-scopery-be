package com.company.scopery.modules.resourcecapacity.threshold.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ThresholdPolicyResponse;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component("resourceCapacityUpsertThresholdPolicyAction")
public class UpsertThresholdPolicyAction {
    private final UtilizationThresholdPolicyRepository repo;
    private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public UpsertThresholdPolicyAction(UtilizationThresholdPolicyRepository repo,
                                       ProjectRepository projects,
                                       CapacityWorkspaceAuthorizationService auth,
                                       CapacityActivityLogger activity) {
        this.repo = repo;
        this.projects = projects;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public ThresholdPolicyResponse executeForWorkspace(UUID workspaceId,
                                                       BigDecimal underAllocatedPercent,
                                                       BigDecimal healthyMinPercent,
                                                       BigDecimal healthyMaxPercent,
                                                       BigDecimal watchMaxPercent,
                                                       BigDecimal overloadedPercent,
                                                       BigDecimal criticalOverloadPercent) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALCULATE);
        var base = repo.findByWorkspaceIdAndProjectIdIsNull(workspaceId)
                .orElseGet(() -> UtilizationThresholdPolicy.defaults(workspaceId, null));
        var updated = new UtilizationThresholdPolicy(
                base.id(), workspaceId, null,
                orElse(underAllocatedPercent, base.underAllocatedPercent()),
                orElse(healthyMinPercent, base.healthyMinPercent()),
                orElse(healthyMaxPercent, base.healthyMaxPercent()),
                orElse(watchMaxPercent, base.watchMaxPercent()),
                orElse(overloadedPercent, base.overloadedPercent()),
                orElse(criticalOverloadPercent, base.criticalOverloadPercent()),
                true, base.version(), base.createdAt(), Instant.now());
        var saved = repo.save(updated);
        activity.logSuccess(CapacityEntityTypes.UTILIZATION_THRESHOLD_POLICY, saved.id(),
                CapacityActivityActions.THRESHOLD_POLICY_UPDATED, "Workspace threshold policy updated");
        return ThresholdPolicyResponse.from(saved);
    }

    @Transactional
    public ThresholdPolicyResponse executeForProject(UUID projectId,
                                                     BigDecimal underAllocatedPercent,
                                                     BigDecimal healthyMinPercent,
                                                     BigDecimal healthyMaxPercent,
                                                     BigDecimal watchMaxPercent,
                                                     BigDecimal overloadedPercent,
                                                     BigDecimal criticalOverloadPercent) {
        var project = projects.findById(projectId)
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        UUID workspaceId = project.workspaceId();
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALCULATE);
        var base = repo.findByProjectId(projectId)
                .orElseGet(() -> UtilizationThresholdPolicy.defaults(workspaceId, projectId));
        var updated = new UtilizationThresholdPolicy(
                base.id(), workspaceId, projectId,
                orElse(underAllocatedPercent, base.underAllocatedPercent()),
                orElse(healthyMinPercent, base.healthyMinPercent()),
                orElse(healthyMaxPercent, base.healthyMaxPercent()),
                orElse(watchMaxPercent, base.watchMaxPercent()),
                orElse(overloadedPercent, base.overloadedPercent()),
                orElse(criticalOverloadPercent, base.criticalOverloadPercent()),
                true, base.version(), base.createdAt(), Instant.now());
        var saved = repo.save(updated);
        activity.logSuccess(CapacityEntityTypes.UTILIZATION_THRESHOLD_POLICY, saved.id(),
                CapacityActivityActions.THRESHOLD_POLICY_UPDATED, "Project threshold policy updated for " + projectId);
        return ThresholdPolicyResponse.from(saved);
    }

    private static BigDecimal orElse(BigDecimal value, BigDecimal fallback) {
        return value != null ? value : fallback;
    }
}
