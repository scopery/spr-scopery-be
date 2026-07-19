package com.company.scopery.modules.resourcecapacity.workload.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimateRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.workload.application.response.WorkloadSnapshotApiResponse;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshot;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshotRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateWorkloadSnapshotAction {
    private final WorkloadSnapshotRepository snapshots;
    private final EffortEstimateRepository estimates;
    private final ProjectResourceAllocationRepository allocations;
    private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public CreateWorkloadSnapshotAction(
            WorkloadSnapshotRepository snapshots,
            EffortEstimateRepository estimates,
            ProjectResourceAllocationRepository allocations,
            ProjectRepository projects,
            CapacityWorkspaceAuthorizationService auth,
            CapacityActivityLogger activity) {
        this.snapshots = snapshots;
        this.estimates = estimates;
        this.allocations = allocations;
        this.projects = projects;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public WorkloadSnapshotApiResponse execute(UUID projectId) {
        var project = projects.findById(projectId)
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        BigDecimal estimated = estimates.findActiveByProjectId(projectId).stream()
                .map(e -> e.effortHours() == null ? BigDecimal.ZERO : e.effortHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal allocated = allocations.findActiveByProjectId(projectId).stream()
                .map(a -> a.allocationPercent() == null ? BigDecimal.ZERO : a.allocationPercent())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gap = estimated.subtract(allocated);
        var snap = snapshots.save(WorkloadSnapshot.create(
                project.workspaceId(),
                projectId,
                allocated,
                allocated,
                estimated,
                estimated,
                BigDecimal.ZERO,
                gap,
                gap.signum() > 0 ? 1 : 0,
                null,
                "MANUAL"));
        activity.logSuccess(
                CapacityEntityTypes.WORKLOAD_SNAPSHOT,
                snap.id(),
                CapacityActivityActions.WORKLOAD_SNAPSHOT_CREATED,
                "Snapshot created");
        return WorkloadSnapshotApiResponse.from(snap);
    }
}
