package com.company.scopery.modules.resourcecapacity.planning.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceCostInputResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.service.ResourcePlanningRebuildService;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RebuildCostInputAction {
    private final ResourcePlanningRebuildService rebuild; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    private final ApplicationEventPublisher events;
    public RebuildCostInputAction(ResourcePlanningRebuildService rebuild, ProjectRepository projects,
                                  CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity,
                                  ApplicationEventPublisher events) {
        this.rebuild=rebuild; this.projects=projects; this.auth=auth; this.activity=activity; this.events=events;
    }
    @Transactional
    public ResourceCostInputResponse execute(UUID projectId, boolean includeSensitive) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        if (includeSensitive) {
            try { auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.PROFITABILITY_SUMMARY_VIEW); }
            catch (RuntimeException ex) { throw CapacityExceptions.costSensitiveAccessDenied(); }
        }
        var result = rebuild.rebuildCostInput(project.workspaceId(), projectId, includeSensitive);
        activity.logSuccess(CapacityEntityTypes.RESOURCE_COST_INPUT, projectId, CapacityActivityActions.RESOURCE_COST_INPUT_REBUILT, "Cost input rebuilt");
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "RESOURCE_COST_INPUT_REBUILT"));
        return result;
    }
}
