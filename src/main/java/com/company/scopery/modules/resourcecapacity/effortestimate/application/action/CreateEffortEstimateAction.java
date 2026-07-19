package com.company.scopery.modules.resourcecapacity.effortestimate.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.effortestimate.application.response.EffortEstimateResponse;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.enums.EffortEstimateType;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.*;
import com.company.scopery.modules.resourcecapacity.effortestimate.http.request.CreateEffortEstimateRequest;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateEffortEstimateAction {
    private final EffortEstimateRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public CreateEffortEstimateAction(EffortEstimateRepository repo, ProjectRepository projects,
                                      CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public EffortEstimateResponse execute(UUID projectId, CreateEffortEstimateRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        if (r.effortHours() == null || r.effortHours().signum() < 0) throw CapacityExceptions.effortInvalidHours();
        var type = CapacityEnumParser.parseRequired(EffortEstimateType.class, r.estimateType(), "EFFORT_ESTIMATE_INVALID_TYPE", "estimateType");
        if (type == EffortEstimateType.REVISED || type == EffortEstimateType.REMAINING) {
            for (var existing : repo.findActiveByProjectId(projectId)) {
                if (existing.targetType().equals(r.targetType()) && existing.targetId().equals(r.targetId())) {
                    repo.save(existing.supersede());
                }
            }
        }
        var saved = repo.save(EffortEstimate.create(project.workspaceId(), projectId, r.targetType(), r.targetId(),
                type, r.effortHours(), r.resourceRoleId(), r.resourceProfileId(), r.reason()));
        activity.logSuccess(CapacityEntityTypes.EFFORT_ESTIMATE, saved.id(), CapacityActivityActions.EFFORT_ESTIMATE_CREATED,
                "Effort estimate created: " + saved.effortHours() + "h");
        return EffortEstimateResponse.from(saved);
    }
}
