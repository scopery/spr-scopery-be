package com.company.scopery.modules.resourcecapacity.planning.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceRiskFlagResponse;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlag;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateResourceRiskFlagAction {
    private final ResourceRiskFlagRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public CreateResourceRiskFlagAction(ResourceRiskFlagRepository repo, ProjectRepository projects,
                                        CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ResourceRiskFlagResponse execute(UUID projectId, String reason, String impactType, String description, UUID resourceProfileId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var saved = repo.save(ResourceRiskFlag.open(project.workspaceId(), projectId, resourceProfileId, reason, impactType, description));
        activity.logSuccess(CapacityEntityTypes.RESOURCE_RISK_FLAG, saved.id(), CapacityActivityActions.RESOURCE_RISK_FLAG_CREATED, reason);
        return new ResourceRiskFlagResponse(saved.id(), saved.projectId(), saved.riskReason(), saved.impactType(), saved.status(), saved.description());
    }
}
