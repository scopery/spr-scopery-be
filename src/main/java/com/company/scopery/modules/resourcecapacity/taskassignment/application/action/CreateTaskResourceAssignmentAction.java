package com.company.scopery.modules.resourcecapacity.taskassignment.application.action;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.response.TaskResourceAssignmentResponse;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.enums.TaskAssignmentType;
import com.company.scopery.modules.resourcecapacity.taskassignment.domain.model.*;
import com.company.scopery.modules.resourcecapacity.taskassignment.http.request.CreateTaskResourceAssignmentRequest;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateTaskResourceAssignmentAction {
    private final TaskResourceAssignmentRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public CreateTaskResourceAssignmentAction(TaskResourceAssignmentRepository repo, ProjectRepository projects,
                                              CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.projects=projects; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public TaskResourceAssignmentResponse execute(UUID projectId, UUID taskId, CreateTaskResourceAssignmentRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var type = CapacityEnumParser.parseRequired(TaskAssignmentType.class, r.assignmentType(), "TASK_ASSIGNMENT_INVALID_TYPE", "assignmentType");
        if (repo.existsActive(taskId, r.resourceProfileId(), type.name())) throw CapacityExceptions.taskAssignmentDuplicate();
        var saved = repo.save(TaskResourceAssignment.create(project.workspaceId(), projectId, taskId, r.resourceProfileId(), type, r.plannedEffortHours()));
        activity.logSuccess(CapacityEntityTypes.TASK_RESOURCE_ASSIGNMENT, saved.id(), CapacityActivityActions.TASK_RESOURCE_ASSIGNED, "Assigned");
        return TaskResourceAssignmentResponse.from(saved);
    }
}
