package com.company.scopery.modules.resourcecapacity.taskassignment.application.service;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.response.TaskResourceAssignmentResponse;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.TaskResourceAssignmentRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TaskResourceAssignmentQueryService {
    private final TaskResourceAssignmentRepository repo; private final ProjectRepository projects; private final CapacityWorkspaceAuthorizationService auth;
    public TaskResourceAssignmentQueryService(TaskResourceAssignmentRepository repo, ProjectRepository projects, CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<TaskResourceAssignmentResponse> listByTask(UUID projectId, UUID taskId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return repo.findActiveByTaskId(taskId).stream().map(TaskResourceAssignmentResponse::from).toList();
    }
}
