package com.company.scopery.modules.project.taskdependency.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Component
public class CreateTaskDependencyAction {

    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskRepository taskRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public CreateTaskDependencyAction(TaskDependencyRepository taskDependencyRepository,
                                      TaskRepository taskRepository,
                                      ProjectActivityLogger activityLogger,
                                      ProjectWorkspaceAuthorizationService authorizationService,
                                      ProjectMutationGuard mutationGuard,
                                      ProjectPlatformPublisher platformPublisher) {
        this.taskDependencyRepository = taskDependencyRepository;
        this.taskRepository = taskRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public TaskDependencyResponse execute(CreateTaskDependencyCommand cmd) {
        authorizationService.requireTaskDependencyCreate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        if (cmd.predecessorTaskId().equals(cmd.successorTaskId())) {
            throw ProjectExceptions.taskDependencySelfReference(cmd.predecessorTaskId());
        }

        TaskDependencyType type = ProjectEnumParser.parseRequired(
                TaskDependencyType.class, cmd.dependencyType(),
                "TASK_DEP_INVALID_TYPE", "dependencyType");

        Task predecessor = taskRepository.findById(cmd.predecessorTaskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.predecessorTaskId()));

        Task successor = taskRepository.findById(cmd.successorTaskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.successorTaskId()));

        if (!predecessor.projectId().equals(cmd.projectId()) || !successor.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskDependencyDifferentProjects(cmd.predecessorTaskId(), cmd.successorTaskId());
        }

        assertEligibleForDependency(predecessor);
        assertEligibleForDependency(successor);

        if (taskDependencyRepository.existsByPredecessorAndSuccessorAndType(
                cmd.predecessorTaskId(), cmd.successorTaskId(), type)) {
            throw ProjectExceptions.taskDependencyAlreadyExists(cmd.predecessorTaskId(), cmd.successorTaskId());
        }

        if (wouldCreateCycle(cmd.predecessorTaskId(), cmd.successorTaskId())) {
            throw ProjectExceptions.taskDependencyCircular();
        }

        TaskDependency dep = TaskDependency.create(
                cmd.projectId(),
                cmd.predecessorTaskId(),
                cmd.successorTaskId(),
                type,
                cmd.lagDays());

        TaskDependency saved = taskDependencyRepository.save(dep);

        platformPublisher.enqueueDependency(saved, "TASK_DEPENDENCY_CREATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK_DEPENDENCY,
                saved.id(),
                ProjectActivityActions.CREATE_TASK_DEPENDENCY,
                "Task dependency created: " + saved.predecessorTaskId() + " -> " + saved.successorTaskId());

        return TaskDependencyResponse.from(saved);
    }

    private void assertEligibleForDependency(Task task) {
        TaskStatus status = task.status();
        if (status == TaskStatus.ARCHIVED || status == TaskStatus.CANCELLED || status == TaskStatus.DONE) {
            throw ProjectExceptions.taskDependencyTaskNotEligible(task.id(), status.name());
        }
    }

    /**
     * Adding predecessor → successor creates a cycle if successor can already reach predecessor
     * by following outgoing edges (predecessorTaskId → successorTaskId).
     */
    private boolean wouldCreateCycle(UUID predecessorId, UUID successorId) {
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(successorId);
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            if (current.equals(predecessorId)) {
                return true;
            }
            if (!visited.add(current)) {
                continue;
            }
            taskDependencyRepository.findActiveDependenciesOutgoing(current)
                    .forEach(d -> queue.add(d.successorTaskId()));
        }
        return false;
    }
}
