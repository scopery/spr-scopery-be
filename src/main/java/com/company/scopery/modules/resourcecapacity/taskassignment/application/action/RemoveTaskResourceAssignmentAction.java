package com.company.scopery.modules.resourcecapacity.taskassignment.application.action;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.response.TaskResourceAssignmentResponse;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.TaskResourceAssignmentRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RemoveTaskResourceAssignmentAction {
    private final TaskResourceAssignmentRepository repo; private final CapacityWorkspaceAuthorizationService auth;
    public RemoveTaskResourceAssignmentAction(TaskResourceAssignmentRepository repo, CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.auth=auth;
    }
    @Transactional
    public TaskResourceAssignmentResponse execute(UUID projectId, UUID taskId, UUID assignmentId, UUID actorId) {
        var a = repo.findById(assignmentId).orElseThrow(() -> CapacityExceptions.taskAssignmentNotFound(assignmentId));
        if (!a.projectId().equals(projectId) || !a.taskId().equals(taskId)) throw CapacityExceptions.taskAssignmentNotFound(assignmentId);
        auth.requireWorkspacePermission(a.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return TaskResourceAssignmentResponse.from(repo.save(a.remove(actorId)));
    }
}
