package com.company.scopery.modules.resourcecapacity.planning.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ProjectCapacitySummaryResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.service.ResourcePlanningRebuildService;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RebuildCapacitySummaryAction {
    private final ResourcePlanningRebuildService rebuild; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public RebuildCapacitySummaryAction(ResourcePlanningRebuildService rebuild, ProjectRepository projects,
                                        CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.rebuild=rebuild; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ProjectCapacitySummaryResponse execute(UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var result = rebuild.rebuildProjectCapacity(project.workspaceId(), projectId);
        activity.logSuccess(CapacityEntityTypes.PROJECT_CAPACITY_SUMMARY, projectId, CapacityActivityActions.CAPACITY_SUMMARY_REBUILT, "Capacity summary rebuilt");
        return result;
    }
}
