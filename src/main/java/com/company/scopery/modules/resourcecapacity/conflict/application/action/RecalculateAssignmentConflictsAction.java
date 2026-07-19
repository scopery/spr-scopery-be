package com.company.scopery.modules.resourcecapacity.conflict.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.conflict.application.response.AssignmentConflictApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflict;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflictRepository;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimateRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RecalculateAssignmentConflictsAction {
    private final AssignmentConflictRepository conflicts;
    private final EffortEstimateRepository estimates;
    private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public RecalculateAssignmentConflictsAction(
            AssignmentConflictRepository conflicts,
            EffortEstimateRepository estimates,
            ProjectRepository projects,
            CapacityWorkspaceAuthorizationService auth,
            CapacityActivityLogger activity) {
        this.conflicts = conflicts;
        this.estimates = estimates;
        this.projects = projects;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public List<AssignmentConflictApiResponse> execute(UUID projectId) {
        var project = projects.findById(projectId)
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        List<AssignmentConflictApiResponse> created = new ArrayList<>();
        if (estimates.findActiveByProjectId(projectId).isEmpty()) {
            var c = conflicts.save(AssignmentConflict.detect(
                    project.workspaceId(), projectId, null, "MISSING_ESTIMATE", "MEDIUM", "No active estimates"));
            activity.logSuccess(
                    CapacityEntityTypes.ASSIGNMENT_CONFLICT,
                    c.id(),
                    CapacityActivityActions.ASSIGNMENT_CONFLICT_DETECTED,
                    c.conflictType());
            created.add(AssignmentConflictApiResponse.from(c));
        }
        return created;
    }
}
