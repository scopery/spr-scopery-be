package com.company.scopery.modules.resourcecapacity.conflict.application.action;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.conflict.application.response.AssignmentConflictApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.*;
import com.company.scopery.modules.resourcecapacity.conflict.http.request.CreateAssignmentConflictRequest;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DetectAssignmentConflictAction {
    private final AssignmentConflictRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public DetectAssignmentConflictAction(AssignmentConflictRepository repo, ProjectRepository projects,
                                          CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public AssignmentConflictApiResponse execute(UUID projectId, CreateAssignmentConflictRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var saved = repo.save(AssignmentConflict.detect(project.workspaceId(), projectId, r.resourceProfileId(),
                r.conflictType(), r.severity(), r.description()));
        activity.logSuccess(CapacityEntityTypes.ASSIGNMENT_CONFLICT, saved.id(), CapacityActivityActions.ASSIGNMENT_CONFLICT_DETECTED,
                "Assignment conflict detected: " + saved.conflictType());
        return AssignmentConflictApiResponse.from(saved);
    }
}
