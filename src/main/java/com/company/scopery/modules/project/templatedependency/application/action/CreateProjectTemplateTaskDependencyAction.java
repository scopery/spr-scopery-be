package com.company.scopery.modules.project.templatedependency.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatedependency.application.command.CreateProjectTemplateTaskDependencyCommand;
import com.company.scopery.modules.project.templatedependency.application.response.ProjectTemplateTaskDependencyResponse;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependency;
import com.company.scopery.modules.project.templatedependency.domain.model.ProjectTemplateTaskDependencyRepository;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Component
public class CreateProjectTemplateTaskDependencyAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplateTaskDependencyRepository dependencyRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public CreateProjectTemplateTaskDependencyAction(
            ProjectTemplateRepository templateRepository,
            ProjectTemplateTaskRepository taskRepository,
            ProjectTemplateTaskDependencyRepository dependencyRepository,
            TemplateVersionMutationGuard mutationGuard,
            TemplateAccessSupport authorizationSupport,
            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateTaskDependencyResponse execute(CreateProjectTemplateTaskDependencyCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        if (cmd.predecessorTemplateTaskId().equals(cmd.successorTemplateTaskId())) {
            throw ProjectExceptions.projectTemplateDependencySelfNotAllowed(cmd.predecessorTemplateTaskId());
        }

        TaskDependencyType type = ProjectEnumParser.parseOptional(
                TaskDependencyType.class, cmd.dependencyType(),
                "PROJECT_TEMPLATE_DEPENDENCY_INVALID_TYPE", "dependencyType");
        if (type == null) {
            type = TaskDependencyType.FINISH_TO_START;
        }

        ProjectTemplateTask pred = taskRepository.findById(cmd.predecessorTemplateTaskId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateTaskNotFound(cmd.predecessorTemplateTaskId()));
        ProjectTemplateTask succ = taskRepository.findById(cmd.successorTemplateTaskId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateTaskNotFound(cmd.successorTemplateTaskId()));

        if (!pred.templateVersionId().equals(cmd.versionId()) || !succ.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateDependencyTaskMismatch();
        }

        if (dependencyRepository.existsByPredecessorAndSuccessorAndType(
                cmd.predecessorTemplateTaskId(), cmd.successorTemplateTaskId(), type)) {
            throw ProjectExceptions.projectTemplateDependencyDuplicate(
                    cmd.predecessorTemplateTaskId(), cmd.successorTemplateTaskId());
        }

        if (wouldCreateCycle(cmd.predecessorTemplateTaskId(), cmd.successorTemplateTaskId())) {
            throw ProjectExceptions.projectTemplateDependencyCycleDetected();
        }

        ProjectTemplateTaskDependency saved = dependencyRepository.save(ProjectTemplateTaskDependency.create(
                cmd.versionId(),
                cmd.predecessorTemplateTaskId(),
                cmd.successorTemplateTaskId(),
                type,
                cmd.lagDays()
        ));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_TASK_DEPENDENCY,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED,
                "Template dependency created"
        );

        return ProjectTemplateTaskDependencyResponse.from(saved);
    }

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
            dependencyRepository.findOutgoingDependencies(current)
                    .forEach(d -> queue.add(d.successorTemplateTaskId()));
        }
        return false;
    }
}
