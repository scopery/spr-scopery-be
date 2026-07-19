package com.company.scopery.modules.resourcecapacity.actualeffort.application.action;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.response.ActualEffortRecordResponse;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.ActualEffortRecordRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelActualEffortAction {
    private final ActualEffortRecordRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    public CancelActualEffortAction(ActualEffortRecordRepository repo, ProjectRepository projects,
                                    CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional
    public ActualEffortRecordResponse execute(UUID projectId, UUID recordId, UUID actorId, String reason) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var rec = repo.findById(recordId).orElseThrow(() -> CapacityExceptions.actualEffortNotFound(recordId));
        if (!projectId.equals(rec.projectId())) throw CapacityExceptions.actualEffortNotFound(recordId);
        try { return ActualEffortRecordResponse.from(repo.save(rec.cancel(actorId, reason))); }
        catch (IllegalStateException ex) { throw CapacityExceptions.actualEffortNotFound(recordId); }
    }
}
